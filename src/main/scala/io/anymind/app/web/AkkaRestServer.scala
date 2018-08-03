package io.anymind.app.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, MalformedRequestContentRejection, RejectionHandler}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.anymind.app.NonBlockingExecContext
import io.anymind.app.calculator.ParallelCalculator
import io.anymind.app.web.command.CalculateCommand
import io.anymind.app.web.dto.{RestError, Result}
import io.anymind.app.web.json.JsonSerializers

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AkkaRestServer(host: RestServerHost,
                     port: RestServerPort,
                     nonBlockingExecContext: NonBlockingExecContext,
                     parallelCalculator: ParallelCalculator)
                    (implicit val actorSystem: ActorSystem, implicit val materializer: ActorMaterializer)
  extends Directives
    with ErrorAccumulatingCirceSupport
    with StrictLogging
    with JsonSerializers {

  implicit private val context: ExecutionContext = nonBlockingExecContext.ec

  implicit def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MalformedRequestContentRejection(msg, _) =>
        complete((BadRequest, RestError("Malformed content")))
      }
      .result()

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex: Throwable =>
        extractUri { uri =>
          logger.error(s"Internal server error while processing request $uri", ex)
          complete((InternalServerError, RestError("Internal server error")))
        }
    }

  lazy val routes = {
    path("evaluate") {
      post {
        entity(as[CalculateCommand]) { command =>
          parallelCalculator.evaluateExpression(command.expression) match {
            case Left(error) => logger.info(s"Parsing error ${error}"); complete((BadRequest, RestError("Expression is unparsable")))
            case Right(future) => onComplete(future) {
              case Success(result) => logger.info(s"Returning result ${result.value}"); complete((OK, Result(result.value)))
              case Failure(error) => logger.info(s"Internal server error ${error}"); complete((InternalServerError, RestError(error.getMessage)))
            }
          }
        }
      }
    }
  }

  private val bindingFuture = {
    logger.info(s"Starting Akka HTTP server on ${host.value}:${port.value}")
    Http().bindAndHandle(routes, host.value, port.value).andThen {
      case Success(_) => logger.info("Akka HTTP server is up")
      case Failure(ex) => logger.error("Akka HTTP server failed to start", ex)
    }
  }

  def started: Future[Unit] = bindingFuture.map(_ => ())

  def stop(): Future[Unit] = {
    logger.info("Stopping Akka HTTP server")
    bindingFuture.flatMap(_.unbind())
  }
}

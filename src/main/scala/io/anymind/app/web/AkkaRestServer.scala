package io.anymind.app.web

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, NoContent}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, MalformedRequestContentRejection, RejectionHandler}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.anymind.app.NonBlockingExecContext
import io.anymind.app.web.command.CalculateCommand
import io.anymind.app.web.dto.RestError
import io.anymind.app.web.json.JsonSerializers

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AkkaRestServer(host: RestServerHost,
                     port: RestServerPort,
                     actorSystem: ActorSystem,
                     nonBlockingExecContext: NonBlockingExecContext)
  extends Directives
    with ErrorAccumulatingCirceSupport
    with StrictLogging
    with JsonSerializers {

  implicit private val as: ActorSystem = actorSystem
  implicit private val context: ExecutionContext = nonBlockingExecContext.ec
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  private val settings = CorsSettings.defaultSettings.copy(allowGenericHttpRequests = false)

  implicit def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MalformedRequestContentRejection(msg, _) =>
        complete((BadRequest, RestError(msg)))
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

  implicit val timeout = Timeout(30L, TimeUnit.SECONDS)

  lazy val routes = {
    path("evaluate") {
      //      cors(settings) {
      post {
        entity(as[CalculateCommand]) { command =>
          complete {
            logger.info("wchodze")

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
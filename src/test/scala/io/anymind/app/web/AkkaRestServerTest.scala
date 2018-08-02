package io.anymind.app.web

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.anymind.app.{NonBlockingExecContext, TestAppSpec}
import io.circe.Json
import org.scalatest.Inside

import scala.concurrent.Await
import scala.concurrent.duration._

class AkkaRestServerTest extends TestAppSpec
  with ScalatestRouteTest
  with Inside
  with ErrorAccumulatingCirceSupport {

  lazy val actorSystem: ActorSystem = ActorSystem("test")

  private val server: AkkaRestServer = new AkkaRestServer(
    host = RestServerHost("localhost").get,
    port = RestServerPort(8080).get,
    actorSystem = actorSystem,
    nonBlockingExecContext = NonBlockingExecContext(actorSystem.dispatcher),
  )
  val routes: Route = server.routes
  implicit val exceptionHandler = server.exceptionHandler
  implicit val rejectionHandler = server.rejectionHandler

  Await.result(server.stop(), 1.seconds)

  feature("Evaluate expression") {
    scenario("Endpoint /evaulate is called with correct json") {

      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("5+10/3"))

      When("http request /evaluate is made with correct json")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("expression should be calculated and status should be OK")
        status shouldEqual OK
      }
    }
  }
}

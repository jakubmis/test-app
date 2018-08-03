package io.anymind.app.web


import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.anymind.app.calculator.ParallelCalculator
import io.anymind.app.{NonBlockingExecContext, TestAppSpec}
import io.circe.Json
import org.scalatest.Inside

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class AkkaRestServerErrorTest extends TestAppSpec
  with ScalatestRouteTest
  with Inside
  with ErrorAccumulatingCirceSupport {

  implicit lazy val actorSystem: ActorSystem = ActorSystem("test")
  private val calculator = stub[ParallelCalculator]

  private val server: AkkaRestServer = new AkkaRestServer(
    host = RestServerHost("localhost").get,
    port = RestServerPort(8080).get,
    nonBlockingExecContext = NonBlockingExecContext(actorSystem.dispatcher),
    calculator
  )
  val routes: Route = server.routes
  implicit val exceptionHandler = server.exceptionHandler
  implicit val rejectionHandler = server.rejectionHandler

  Await.result(server.stop(), 1.seconds)

  feature("Rest server error handling") {
    scenario("Endpoint /evaulate is called and calculator fails") {

      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("5+10/5"))

      And("a mocked parallel calculator with not calculated expression")
      (calculator.evaluateExpression(_: String))
        .when("5+10/5")
        .returns(Right(Future.failed(new RuntimeException)))

      When("http request /evaluate is made")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("status should be InternalServerError")
        status shouldBe InternalServerError
      }
    }

    scenario("Endpoint /evaulate is called and runtime exception occurs") {

      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("5+10/5"))

      And("a mocked parallel calculator which throws Exception")
      (calculator.evaluateExpression(_: String))
        .when("5+10/5")
        .throws(new RuntimeException())

      When("http request /evaluate is made")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("status should be InternalServerError")
        status shouldBe InternalServerError
      }
    }
  }
}

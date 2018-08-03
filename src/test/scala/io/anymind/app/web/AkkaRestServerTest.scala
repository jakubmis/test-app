package io.anymind.app.web

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Route.seal
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.anymind.app.calculator.{MathExpressionParser, ParallelCalculator}
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
    nonBlockingExecContext = NonBlockingExecContext(actorSystem.dispatcher),
    new ParallelCalculator(new MathExpressionParser())
  )
  val routes: Route = server.routes
  implicit val exceptionHandler = server.exceptionHandler
  implicit val rejectionHandler = server.rejectionHandler

  Await.result(server.stop(), 1.seconds)

  feature("Evaluate expression") {
    scenario("Endpoint /evaulate is called with correct json") {

      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("5+10/5"))

      When("http request /evaluate is made with correct json")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("expression should be calculated and status should be OK")
        status shouldBe OK
        responseAs[Json] shouldBe Json.obj("result" -> Json.fromDouble(7.0).get)
      }
    }

    scenario("Endpoint /evaulate is called with corrupted equation") {
      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("5+10/0"))

      When("http request /evaluate is made with corrupted equation")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("status should be OK")
        status shouldBe OK
        responseAs[Json] shouldBe Json.obj("result" -> Json.fromString("Infinity"))
      }
    }

    scenario("Endpoint /evaulate is called with malformed content") {
      Given("an instance of CalculateCommand json")
      val incorrectJson: Json = Json.obj(
        "id" -> Json.fromString("39a97848-c0e0-4850-8c35-4ec5a7969cd4"))

      When("http request /evaluate is made with malformed content")
      Post("/evaluate", HttpEntity(`application/json`, incorrectJson.toString())) ~> seal(routes) ~> check {
        Then("status should be BadRequest and error message should be shown")
        status shouldBe BadRequest
        responseAs[Json] shouldBe Json.obj("error" -> Json.fromString("Malformed content"))
      }
    }

    scenario("Endpoint /evaulate is called with incorrect expression") {

      Given("an instance of CalculateCommand json")
      val json: Json = Json.obj("expression" -> Json.fromString("abc"))

      When("http request /evaluate is made with incorrect expression")
      Post("/evaluate", HttpEntity(`application/json`, json.toString())) ~> routes ~> check {
        Then("status should be BadRequest and error message should be shown")
        status shouldBe BadRequest
        responseAs[Json] shouldBe Json.obj("error" -> Json.fromString("Expression is unparsable"))
      }
    }
  }
}

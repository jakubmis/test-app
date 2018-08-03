package io.anymind.app.calculator

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.anymind.app.TestAppSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class ParallelCalculatorTest extends TestAppSpec {

  implicit lazy val actorSystem: ActorSystem = ActorSystem("test")
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

  feature("testEvaluateExpression") {
    scenario("Parsing correct expression") {
      Given("an instance of ParallelCalculator class")
      val calculator = new ParallelCalculator(new MathExpressionParser)
      When("evaluate expression request is made with correct string expression")
      val result = calculator.evaluateExpression("5+6*9+(10+8)*19-2")
      Then("the expression is correctly calculated")
      result.isRight shouldBe true
      val number = Await.result(result.getOrElse(null), 1.seconds)
      number shouldBe Number(399)
    }

    scenario("Parsing incorrect expression") {
      Given("an instance of ParallelCalculator class")
      val calculator = new ParallelCalculator(new MathExpressionParser)
      When("evaluate expression request is made with incorrect string expression")
      val result = calculator.evaluateExpression("abc+234")
      Then("the error is returned")
      result.isLeft shouldBe true
    }
  }
}

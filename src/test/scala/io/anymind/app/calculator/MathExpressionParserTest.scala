package io.anymind.app.calculator

import io.anymind.app.TestAppSpec

class MathExpressionParserTest extends TestAppSpec {

  feature("Parser can parse string expression or return error") {
    scenario("Parsing correct expression") {
      Given("an instance of StringParser class")
      val parser = new MathExpressionParser()
      When("parsing expression request is made with correct string expression")
      val value = parser.parseExpression("5/5+6*6+(5+6)-1")
      Then("expression is parsed into Operation tree")
      value.isRight shouldBe true
      value.getOrElse(null) shouldBe a[Operation]
    }

    scenario("Parsing incorrect expression") {
      Given("an instance of StringParser class")
      val parser = new MathExpressionParser()
      When("parsing expression request is made with incorrect string expression")
      val value = parser.parseExpression("abc+yasd")
      Then("error is returned")
      value.isLeft shouldBe true
    }
  }
}

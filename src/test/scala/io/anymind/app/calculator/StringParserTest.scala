package io.anymind.app.calculator

import io.anymind.app.TestAppSpec

class StringParserTest extends TestAppSpec {

  feature("Parser can successfully parse string expression") {
    Given("an instance of StringParser class")
    val parser = new StringParser()
    When("parsing expression request is made with correct string expression")
    val value = parser.parseExpression("5+6*6+(5+6)").get
    Then("expression is parsed into Operation tree")
    value shouldBe a[Operation]
  }

}

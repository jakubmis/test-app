package io.anymind.app.calculator

import scala.util.parsing.combinator.JavaTokenParsers

class MathExpressionParser extends JavaTokenParsers {

  def parseExpression(expression: String): Either[String, Operation] = parseAll(expressionParser, expression) match {
    case Success(result, _) => Right(result)
    case Failure(error, _) => Left(error)
  }

  private def number: Parser[Operation] = floatingPointNumber ^^ { number => Number(number.toDouble) }

  private def expressionOrNumber: Parser[Operation] = "(" ~> expressionParser <~ ")" | number

  private def expressionParser: Parser[Operation] = multiplyOrDivide ~ rep("+" ~ multiplyOrDivide | "-" ~ multiplyOrDivide) ^^ {
    case number ~ rest => rest.foldLeft(number) {
      case (x, "+" ~ y) => Add(x, y);
      case (x, "-" ~ y) => Subtract(x, y);
    }
  }

  private def multiplyOrDivide: Parser[Operation] = expressionOrNumber ~ rep("*" ~ expressionOrNumber | "/" ~ expressionOrNumber) ^^ {
    case number ~ rest => rest.foldLeft(number) {
      case (x, "*" ~ y) => Multiply(x, y);
      case (x, "/" ~ y) => Divide(x, y);
    }
  }

}

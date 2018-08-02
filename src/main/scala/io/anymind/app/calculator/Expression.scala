package io.anymind.app.calculator


sealed trait Expression

case class Result(result: String) extends Expression
case class Error(error: String) extends Expression

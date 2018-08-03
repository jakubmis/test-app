package io.anymind.app.calculator

sealed trait Operation

case class Multiply(left: Operation, right: Operation) extends Operation
case class Divide(left: Operation, right: Operation) extends Operation
case class Add(left: Operation, right: Operation) extends Operation
case class Subtract(left: Operation, right: Operation) extends Operation

case class Number(value: Double) extends Operation {
  def -(number: Number): Number = {
    Number(this.value - number.value)
  }
  def *(number: Number): Number = {
    Number(this.value * number.value)
  }
  def /(number: Number): Number = {
    Number(this.value / number.value)
  }
  def +(number: Number): Number = {
    Number(this.value + number.value)
  }
}

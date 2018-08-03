package io.anymind.app.calculator

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, SourceShape}

import scala.concurrent.Future

class ParallelCalculator(stringParser: MathExpressionParser) (implicit val actorSystem: ActorSystem,
                                                              implicit val materializer: ActorMaterializer) {

  def evaluateExpression(string: String): Either[String, Future[Number]] = {
    stringParser
      .parseExpression(string)
      .map(computeUsingAkkaStreams(_).run())
  }

  private def computeUsingAkkaStreams(operation: Operation): RunnableGraph[Future[Number]] = {
    source(operation).toMat(Sink.head[Number])(Keep.right)
  }

  private def source(operation: Operation): Source[Number, NotUsed] = {
    operation match {
      case Multiply(left, right) => reduceStatement((_ * _), left, right)
      case Divide(left, right) => reduceStatement((_ / _), left, right)
      case Add(left, right) => reduceStatement((_ + _), left, right)
      case Subtract(left, right) => reduceStatement(_ - _, left, right)
    }
  }

  private def reduceStatement(function: (Number, Number) => Number, left: Operation, right: Operation): Source[Number, NotUsed] = {
    (left, right) match {
      case (x: Number, y: Number) => leafSource(function, Source.single(x), Source.single(y))
      case (x: Operation, y: Number) => leafSource(function, source(x), Source.single(y))
      case (x: Number, y: Operation) => leafSource(function, Source.single(x), source(y))
      case (x: Operation, y: Operation) => leafSource(function, source(x), source(y))
    }
  }

  private def leafSource(function: (Number, Number) => Number, leftNumber: Source[Number, NotUsed], rightNumber: Source[Number, NotUsed]): Source[Number, NotUsed] = {
    Source.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val zip = builder.add(ZipWith[Number, Number, Number](function))
      leftNumber ~> zip.in0
      rightNumber ~> zip.in1
      SourceShape(zip.out)
    }.async)
  }
}

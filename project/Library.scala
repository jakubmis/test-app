import sbt._

object Library {

  private val akkaHttpVer = "10.0.10"
  private val circeVersion = "0.9.0-M1"

  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVer
  val akkaHttpTesting = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVer
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % "2.5.14"
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.18.0"
  val corsSupport = "ch.megard" %% "akka-http-cors" % "0.2.2"

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.4"
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"

  val circeCore = "io.circe" %% "circe-core" % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeParser = "io.circe" %% "circe-parser" % circeVersion
  val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeVersion

}

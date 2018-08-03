
organization := "io.anymind.app"

scalaVersion := "2.12.6"

version := "0.1"

scalastyleFailOnError := true

libraryDependencies ++= Vector(
  Library.scalaLogging,
  Library.logback,
  Library.java8Compat,
  Library.akkaHttp,
  Library.akkaHttpCirce,
  Library.akkaStreams,
  Library.circeGeneric,
  Library.circeCore,
  Library.circeGenericExtras,
  Library.circeParser,
  Library.akkaHttpTesting % Test,
  Library.scalatest % Test,
  Library.scalamock % Test,
)
import spray.revolver.RevolverPlugin.Revolver

import AssemblyKeys._

name := "sprack"

scalaVersion := "2.10.1"

version := "0.0.6"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "spray nightly repo" at "http://nightlies.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.2.4",
  "io.spray" % "spray-can" % "1.2-20130620",
  "com.typesafe.akka" %%  "akka-actor" % "2.2.0-RC1",
  "com.typesafe.akka" %%  "akka-slf4j" % "2.2.0-RC1",
  "ch.qos.logback"% "logback-classic" % "1.0.12" % "runtime",
  "org.jruby" % "jruby-complete" % "1.7.4",
  "org.rogach" %% "scallop" % "0.9.2",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

Revolver.settings

assemblySettings

jarName in assembly := "sprack.jar"

mainClass in assembly := Some("com.sclasen.sprack.Main")

test in assembly := {}



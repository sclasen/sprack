import spray.revolver.RevolverPlugin.Revolver

name := "sprack"

scalaVersion := "2.10.1"

version := "0.0.1"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "spray nightly repo" at "http://nightlies.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.2.4",
  "io.spray" % "spray-can" % "1.1-20130513",
  "com.typesafe.akka" %%  "akka-actor" % "2.1.2",
  "com.typesafe.akka" %%  "akka-slf4j" % "2.1.2",
  "ch.qos.logback"% "logback-classic" % "1.0.12" % "runtime",
  "org.jruby" % "jruby-complete" % "1.7.4",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

Revolver.settings

sbtassembly.Plugin.assemblySettings

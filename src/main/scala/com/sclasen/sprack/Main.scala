package com.sclasen.sprack

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import org.rogach.scallop._
import concurrent.duration._
import akka.util.Timeout
import akka.pattern._
import scala.util.{Failure, Success}
import com.typesafe.config.ConfigFactory
import java.io.File

object Main extends App {

  val conf = new Conf(args)
  implicit val system = createActorSystem(conf)
  implicit val bootTimeout = Timeout(conf.timeout())
  // the handler actor replies to incoming HttpRequests
  println("creating RackHandler, this can take a while on big apps")
  val handler = system.actorOf(Props(new SprackService(conf.rackfile(), conf.port())), name = "handler")
  import concurrent.ExecutionContext.Implicits.global
  (handler ? Ready).onComplete {
    case Success(Ready) =>
      println("RackHandler Ready binding")
      IO(Http) ! Http.Bind(handler, interface = conf.host(), port = conf.port())
    case Failure(e) =>
      println("Failed to init RackHandler, exiting")
      e.printStackTrace()
      System.exit(11)
  }


  def createActorSystem(conf: Conf): ActorSystem = {
    conf.akkafile.get.map {
      file =>
        ActorSystem("sprack-system", ConfigFactory.parseFile(new File(file)))
    }.getOrElse(ActorSystem("sprack-system"))
  }

}

class Conf(args: Seq[String]) extends ScallopConf(args) {
  version("0.0.0.1")
  val host = opt[String](default = Some("0.0.0.0"))
  val port = opt[Int](default = Some(8080))
  val rackfile = opt[String](default = Some("./config.ru"))
  val timeout = opt[Int](default = Some(30)).map(_ seconds)
  val akkafile = opt[String]()
}
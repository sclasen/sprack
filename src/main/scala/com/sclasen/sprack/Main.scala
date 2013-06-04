package com.sclasen.sprack

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern._
import spray.can.Http
import org.rogach.scallop._
import concurrent.duration._
import akka.util.Timeout
import scala.util.{Failure, Success, Try}

object Main extends App  {

  val conf = new Conf(args)
  implicit val system = ActorSystem()
  implicit val bootTimeout = Timeout(conf.timeout())
  import concurrent.ExecutionContext.Implicits.global

  // the handler actor replies to incoming HttpRequests
  val handler = system.actorOf(Props(new SprackService(conf.rackfile(), conf.host(), conf.port())), name = "handler")

  (handler ? Ready).onComplete{
    case Success(Ready) =>
      println("RackHandler Ready binding")
      IO(Http) ! Http.Bind(handler, interface = "localhost", port = conf.port())
    case Failure(e) =>
      println("Failed to init RackHandler, exiting")
      e.printStackTrace()
      System.exit(11)
  }


}

class Conf(args:Seq[String]) extends ScallopConf(args){
  val host = opt[String](default = Some("localhost"))
  val port = opt[Int](default = Some(8080))
  val rackfile = opt[String](default = Some("./config.ru"))
  val timeout = opt[Int](default = Some(30)).map(_ seconds)
}
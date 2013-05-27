package com.sclasen.sprack

import akka.actor.Actor
import spray.util.SprayActorLogging
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpRequest
import java.io.File
import concurrent.duration._
import scala.concurrent.Future

class SprackService(config: String = "config.ru") extends Actor with SprayActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  import context.dispatcher

  val rackApp = new RackApp(config)

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case r: HttpRequest => {
      val client = sender
      Future{
        client ! rackApp.call(r).toSpray
      }
    }
  }


}

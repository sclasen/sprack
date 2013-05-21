package com.sclasen.sprack

import akka.actor.Actor
import spray.util.SprayActorLogging
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpRequest
import java.io.File
import concurrent.duration._

class SprackService(config: String = "config.ru") extends Actor with SprayActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'

  val rackApp = new RackApp(config)

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case r: HttpRequest => {
      rackApp.call(r)
    }
  }


}

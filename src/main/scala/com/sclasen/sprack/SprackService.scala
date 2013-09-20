package com.sclasen.sprack

import akka.actor.{Props, Actor}
import spray.util.SprayActorLogging
import spray.can.Http
import spray.http._
import scala.concurrent.future
import spray.http.HttpRequest
import spray.http.ChunkedMessageEnd
import spray.http.HttpResponse
import spray.http.ChunkedResponseStart
import java.io.OutputStream
import java.nio.ByteBuffer

case object Ready

class SprackService(config: String, port: Int) extends Actor with SprayActorLogging {

  implicit val futureDispatcher = context.system.dispatchers.lookup("sprack.rack-dispatcher")
  val out = actorLogStream(System.out, "logs-out")
  val err = actorLogStream(System.err, "logs-err")
  val rackApp = new RackApp(config, port, out, err)

  def actorLogStream(stream: OutputStream, name: String) =
    ActorLogger(context.actorOf(Props(classOf[Logger], stream).withDispatcher("sprack.logger-dispatcher"), name))

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case r: HttpRequest => {
      val client = sender
      future {
        rackApp.call(r) match {
          case Right(response) => client ! response
          case Left((response, chunks)) =>
            client ! ChunkedResponseStart(response)
            chunks.foreach(ch => client ! ch)
            client ! ChunkedMessageEnd()
        }
      }.onFailure {
        case e: Exception =>
          val msg = e.toString + e.getStackTraceString
          err.send(msg.getBytes)
          out.send(msg.getBytes)
          client ! HttpResponse(StatusCodes.InternalServerError, HttpEntity(msg))
      }
    }
    case Ready => sender ! Ready
  }


}

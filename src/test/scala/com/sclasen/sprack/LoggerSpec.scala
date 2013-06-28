package com.sclasen.sprack

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import java.io.{PrintStream, ByteArrayOutputStream}
import akka.actor.Props
import spray.http.{Uri, HttpRequest}
import spray.http.HttpMethods._
import java.nio.ByteBuffer


class LoggerSpec extends WordSpec with MustMatchers with SprackSpec{
    "A logger" must{
      "log" in {
        val out = new ByteArrayOutputStream()
        val logger = ActorLogStream(system.actorOf(Props(classOf[Logger], new PrintStream(out))))
        val logs = "-ERROR->"
        logger.write(ByteBuffer.wrap(logs.getBytes))
        Thread.sleep(1000)
        new String(out.toByteArray) must equal(logs)
      }

      "log from ruby stdout" in {
        val out = new ByteArrayOutputStream()
        val logger = ActorLogStream(system.actorOf(Props(classOf[Logger], new PrintStream(out))))
        val err = ActorLogStream(system.actorOf(Props(classOf[Logger], System.err)))
        val app = new RackApp("src/test/resources/log.ru", 5000, logger, err)
        app.call(HttpRequest(GET, Uri("/")))
        new String(out.toByteArray) must equal("I LOGGED SOMETHING\n")
      }
    }
}

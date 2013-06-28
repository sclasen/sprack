package com.sclasen.sprack

import akka.actor.{Props, ActorSystem}
import org.scalatest.{Suite, BeforeAndAfterAll}


trait SprackSpec extends BeforeAndAfterAll{
  this:Suite =>

  var system:ActorSystem = _

  def rackApp(config:String,port:Int) = {
    val (out,err) = actorLogStreams
    new RackApp(config,port,out,err)
  }

  def actorLogStreams = {
    (ActorLogStream(system.actorOf(Props(classOf[Logger],System.out))),
    ActorLogStream(system.actorOf(Props(classOf[Logger],System.err))))
  }

  override protected def afterAll() {
    super.afterAll()
    system.shutdown()
    system.awaitTermination()
  }

  override protected def beforeAll() {
    system = ActorSystem("sprack-logging")
    super.beforeAll()
  }
}

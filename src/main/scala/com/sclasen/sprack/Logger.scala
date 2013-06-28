package com.sclasen.sprack

import akka.actor.{ActorRef, Actor}
import java.io.OutputStream
import java.nio.channels.WritableByteChannel
import java.nio.ByteBuffer


class Logger(stream: OutputStream) extends Actor {
  def receive = {
    case b: Array[Byte] =>
      stream.write(b)
  }
}

object ActorLogStream {
  def apply(logger: ActorRef) = new ActorLogStream(logger)
}

class ActorLogStream(logger: ActorRef) extends WritableByteChannel {
  def write(src: ByteBuffer): Int = {
    val arr = new Array[Byte](src.remaining())
    src.get(arr)
    logger ! arr
    arr.length
  }

  def isOpen: Boolean = true

  def close() {}
}


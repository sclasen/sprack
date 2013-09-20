package com.sclasen.sprack

import akka.actor.{ActorRef, Actor}
import java.io.OutputStream
import org.jruby.anno.{JRubyMethod, JRubyClass}
import org.jruby.{RubyString, RubyClass, Ruby, RubyObject}
import org.jruby.runtime.builtin.IRubyObject
import org.jruby.runtime.{ObjectAllocator, ThreadContext}
import akka.util.ByteString
import org.jruby.runtime.load.{Library, BasicLibraryService}


class Logger(stream: OutputStream) extends Actor {
  val lineSep = sys.props("line.separator").getBytes
  def receive = {
    case b: Array[Byte] =>
      stream.write(b)
      stream.write(lineSep)
    case bs: ByteString =>
      stream.write(bs.toArray)
      stream.write(lineSep)
  }
}

object ActorLogger {
  def apply(logger: ActorRef) = new ActorLogger(logger)
}

@JRubyClass(name = Array("ActorLogger"))
class ActorLogger(logger: ActorRef) {
   def puts(string:RubyString){
    logger ! string.getBytes
  }

  def send(any:Any) = {
    logger ! any.toString.getBytes
  }

  def write(string:RubyString) = puts(string)
}



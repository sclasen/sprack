package com.sclasen.sprack

import org.jruby.runtime.builtin.IRubyObject
import org.jruby.javasupport.JavaEmbedUtils
import org.jruby.javasupport.JavaEmbedUtils.javaToRuby
import org.jruby.{RubyIO, Ruby, RubyHash, RubyInstanceConfig}
import java.io.File
import collection.JavaConverters._
import java.util.{List => JList, HashMap => JHMap}
import spray.http._
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import akka.util.ByteString
import spray.http.parser.HttpParser
import scala.reflect._
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.annotation.tailrec
import RackApp._
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap


class RackApp(config: String, port: Int, out: ActorLogStream, err: ActorLogStream) {

  val configFile = new File(config)
  implicit val runtime = JavaEmbedUtils.initialize(List(configFile.getParentFile.getCanonicalPath).asJava, runtimeConfig)
  val adapter = JavaEmbedUtils.newObjectAdapter()
  loadRack
  val app = loadRackApp

  def runtimeConfig: RubyInstanceConfig = {
    val config = new RubyInstanceConfig()
    val classLoader = Option(Thread.currentThread().getContextClassLoader).getOrElse(ClassLoader.getSystemClassLoader)
    config.setClassCache(JavaEmbedUtils.createClassCache(classLoader))
    config
  }

  def loadRack {
    val is = Thread.currentThread().getContextClassLoader.getResourceAsStream("sprack.rb")
    runtime.loadFile("sprack.rb", is, false)
    is.close()
  }

  def loadRackApp = {
    val builder = runtime.evalScriptlet("Sprack::RackServer::Builder.new")
    adapter.callMethod(builder, "build",
      Array[IRubyObject](
        ruby(configFile.getCanonicalPath),
        ruby(port),
        ruby(RubyIO.newIO(runtime, out)),
        ruby(RubyIO.newIO(runtime, err))
      ))
  }

  def ruby[T](any: T) = javaToRuby(runtime, any)


  def call(request: HttpRequest): Either[(HttpResponse, Stream[MessageChunk]), HttpResponse] = {
    val obj = adapter.callMethod(app, "call", Array[IRubyObject](ruby(RackRequest(request)))).convertToArray()

    val status = obj.get(0).asInstanceOf[Long].toInt

    val (errors, headers) = HttpParser.parseHeaders {
      Option(obj.get(1)).map {
        _.asInstanceOf[JList[HttpHeader]].asScala.toList
      }.getOrElse(List.empty)
    }

    errors.foreach(err.send)

    if (header[`Transfer-Encoding`](headers).filter(_.hasChunked).isDefined) {
      val resp = RackResponse(status, headers, None)
      val chunks = Option(obj.get(2)).map {
        bs =>
          bs.asInstanceOf[JList[Array[Byte]]].asScala.toStream.map(b => MessageChunk(b))
      }.getOrElse(Stream.empty[MessageChunk])
      Left(resp.toSpray, chunks)
    } else {
      val body = Option(obj.get(2)).map {
        bs =>
          bs.asInstanceOf[JList[Array[Byte]]].asScala.foldLeft(ByteString.newBuilder) {
            case (builder, bytes) => builder.putBytes(bytes)
          }.result().toArray
      }
      Right(RackResponse(status, headers, body).toSpray)
    }

  }

  /*warning this will system exit, dont call in tests*/
  def stop {
    runtime.tearDown(false)
  }

}

object RackApp {
  def header[T <: HttpHeader : ClassTag](h: List[HttpHeader]): Option[T] = {
    val erasure = classTag[T].runtimeClass
    @tailrec def next(headers: List[HttpHeader]): Option[T] =
      if (headers.isEmpty) None
      else if (erasure.isInstance(headers.head)) Some(headers.head.asInstanceOf[T]) else next(headers.tail)
    next(h)
  }

  def filterHeaders(hs: List[HttpHeader]): List[HttpHeader] = hs.filter {
    h => `Content-Type`.lowercaseName != h.lowercaseName && `Content-Length`.lowercaseName != h.lowercaseName
  }
}

case class RackRequest(method: String, scheme: String, host: String, path: String, query: String, contentType: String, contentLength: String, headers: RubyHash, input: ByteString)

object RackRequest {

  //previously we were doing (in ruby) header.name => HTTP_ + header.name.toUpper.gsub("-","_") translation
  //but that was the top sprack code present in cpu profiling.
  val rackHeaderNameCache = new ConcurrentLinkedHashMap.Builder[String,String]
    .initialCapacity(1024)
    .maximumWeightedCapacity(1024)
    .build.asScala

  def apply(req: HttpRequest)(implicit ruby: Ruby): RackRequest = RackRequest(
    req.method.toString,
    req.uri.scheme.toString,
    req.uri.authority.host.address,
    req.uri.path.toString,
    req.uri.query.toString,
    contentType(req),
    contentLength(req),
    headers(req),
    byteStringBody(req)
  )

  def contentType(req: HttpRequest): String = req.header[`Content-Type`].map(_.value).orNull

  def contentLength(req: HttpRequest): String = req.header[`Content-Length`].map(_.length.toString).orNull

  def byteStringBody(req: HttpRequest): ByteString = ByteString(req.entity.buffer)

  def headers(req: HttpRequest)(implicit ruby: Ruby): RubyHash = req.headers.foldLeft(new RubyHash(ruby)) {
    case (hash, `Content-Length`(_)) => hash
    case (hash, `Content-Type`(_)) => hash
    case (hash, header) => {
      hash.put(header.rackHeader, header.value)
      hash
    }
  }

  implicit class RackHeader(val header: HttpHeader) extends AnyVal {
    def rackHeader = RackRequest.rackHeaderNameCache.getOrElseUpdate(header.lowercaseName, "HTTP_" + header.lowercaseName.toUpperCase.replace('-', '_'))
  }


}

case class RackResponse(status: Int, parsedHeaders: List[HttpHeader], body: Option[Array[Byte]]) {

  def contentType: ContentType = header[`Content-Type`](parsedHeaders).map(_.contentType).getOrElse(`text/plain`)

  def entity: HttpEntity = body.map(bytes => HttpEntity(contentType, bytes)).getOrElse(EmptyEntity)

  def toSpray: HttpResponse = {
    HttpResponse(StatusCodes.getForKey(status).get, entity, filterHeaders(parsedHeaders))
  }

}




package com.sclasen.sprack

import org.jruby.runtime.builtin.IRubyObject
import org.jruby.javasupport.JavaEmbedUtils
import org.jruby.{Ruby, RubyHash, RubyInstanceConfig}
import java.io.{ByteArrayInputStream, InputStream, File}
import collection.JavaConverters._
import spray.http._
import spray.http.HttpHeaders._
import spray.http.ContentType._


class RackApp(config: String) {

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
    adapter.callMethod(builder, "test", Array.empty[IRubyObject])
    adapter.callMethod(builder, "build", Array[IRubyObject](JavaEmbedUtils.javaToRuby(runtime, configFile.getCanonicalPath)))
  }


  def call(request: HttpRequest): RackResponse = {
    val obj = adapter.callMethod(app, "call", Array[IRubyObject](JavaEmbedUtils.javaToRuby(runtime, RackRequest(request)))).convertToArray()
    val status = obj.get(0).asInstanceOf[Long].toInt
    val headers = obj.get(1).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[HttpHeader]).toList
    val body = obj.get(2).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[String]).apply(0)
    RackResponse(status,headers,body)
  }

}

case class RackRequest(method: String, scheme: String, path: String, query: String, contentType: String, contentLength: String, headers: RubyHash, inputStream: InputStream)

object RackRequest {
  def apply(req: HttpRequest)(implicit ruby: Ruby): RackRequest = RackRequest(
    req.method.toString,
    req.uri.scheme.toString,
    req.uri.path.toString,
    req.uri.query.toString,
    contentType(req),
    contentLength(req),
    headers(req),
    inputStream(req)
  )

  def contentType(req: HttpRequest): String = req.header[`Content-Type`].map(_.value).orNull

  def contentLength(req: HttpRequest): String = req.header[`Content-Length`].map(_.length.toString).orNull

  def inputStream(req: HttpRequest): InputStream = new ByteArrayInputStream(req.entity.buffer)

  def headers(req: HttpRequest)(implicit ruby: Ruby): RubyHash = req.headers.foldLeft(new RubyHash(ruby)) {
    case (hash, `Content-Length`(_)) => hash
    case (hash, `Content-Type`(_)) => hash
    case (hash, header) => {
      hash.put(header.name, header.value)
      hash
    }
  }

}

case class RackResponse(status: Int, headers:List[HttpHeader], body: String){
  def toSpray:HttpResponse=HttpResponse(StatusCodes.getForKey(status).get, HttpEntity(body), headers)
}


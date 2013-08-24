package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEncodings, HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.HttpHeaders._
import spray.httpx.ResponseTransformation._
import spray.httpx.encoding.Gzip


class GzipSpec extends WordSpec with MustMatchers with SprackSpec {


  "Apps using Rack::Deflater" must {
    "work" in {
      try {
        val app = rackApp("src/test/resources/gzip.ru", 80)
        val resp = app.call(HttpRequest(GET, Uri("/"), List(`Accept-Encoding`(HttpEncodings.gzip)))).right.get
        resp.status.intValue must equal(200)
        decode(Gzip).apply(resp).entity.asString must equal("Hello. Hows the GZIP workin out?")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }
  }

}

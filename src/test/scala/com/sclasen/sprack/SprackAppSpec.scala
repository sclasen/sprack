package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.ContentType.`application/json`


class SprackAppSpec extends WordSpec with MustMatchers {

  "A sprack app" must {
    "load" in {
      try {
        val app = new RackApp("src/test/resources/config.ru")
        val resp = app.call(HttpRequest(GET, Uri("/test")))
        resp.status must equal(200)
      } catch {
        case e: Exception => fail(e)
      }
    }

    "have proper io" in {
      try {
        val app = new RackApp("src/test/resources/iotest.ru")
        val resp = app.call(HttpRequest(POST, Uri("/test"), entity = HttpEntity(`application/json`, """{"foo":"bar"}""")))
        resp.status must equal(200)
      } catch {
        case e: Exception => fail(e)
      }
    }

  }

}

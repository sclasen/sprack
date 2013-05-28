package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.ContentType.`application/json`


class IOSpec extends WordSpec with MustMatchers {


  "IO" must {
    "have proper io" in {
      try {
        val app = new RackApp("src/test/resources/iotest.ru")
        val resp = app.call(HttpRequest(POST, Uri("/test"), entity = HttpEntity(`application/json`, """{"foo":"bar"}""")))
        resp.status must equal(200)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }
  }

}

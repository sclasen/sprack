package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.ContentTypes._


class IOSpec extends WordSpec with MustMatchers with SprackSpec  {


  "IO" must {
    "have proper io" in {
      try {
        val app = rackApp("src/test/resources/iotest.ru", 80)
        val resp = app.call(HttpRequest(POST, Uri("/test"), entity = HttpEntity(`application/json`, """{"foo":"bar"}"""))).right.get
        resp.status.intValue must equal(200)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }
  }

}

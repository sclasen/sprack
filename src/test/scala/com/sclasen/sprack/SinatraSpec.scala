package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._


class SinatraSpec extends WordSpec with MustMatchers {


  "Sinatra" must {
    "run sinatra" in {
      try {
        val app = new RackApp("src/test/resources/sinatra.ru", 80)
        val resp = app.call(HttpRequest(POST, Uri("/"), entity = HttpEntity("foo"))).right.get
        resp.status.intValue must equal(200)
        resp.entity.asString must equal("Hello posted foo")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }

  }

}

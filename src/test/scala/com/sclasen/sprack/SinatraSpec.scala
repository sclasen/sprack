package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.ContentType.`application/json`


class SinatraSpec extends WordSpec with MustMatchers {


  "Sinatra" must {
    "run sinatra" in {
      try {
        val app = new RackApp("src/test/resources/sinatra.ru")
        val resp = app.call(HttpRequest(POST, Uri("/"), entity = HttpEntity("foo")))
        resp.status must equal(200)
        resp.body.map(b => new String(b)).get must equal("Hello posted foo")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }

  }

}

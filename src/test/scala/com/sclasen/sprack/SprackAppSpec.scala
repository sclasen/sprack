package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{Uri, HttpRequest}
import spray.http.HttpMethods._


class SprackAppSpec extends WordSpec with MustMatchers with SprackSpec  {


  "A sprack app" must {

    "load" in {
      try {
        val app = rackApp("src/test/resources/config.ru", 80)
        val resp = app.call(HttpRequest(GET, Uri("/test"))).right.get
        resp.status.intValue must equal(200)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }

  }

}

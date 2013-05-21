package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{Uri, HttpRequest}
import spray.http.HttpMethods._



class SprackAppSpec extends WordSpec with MustMatchers {

  "A sprack app" must {
    "load" in {
      try{
        val app = new RackApp("src/test/resources/config.ru")
        val resp = app.call(HttpRequest(GET, Uri("/test")))
        println(resp)
      } catch {
        case e => e.printStackTrace()
      }
    }
  }

}

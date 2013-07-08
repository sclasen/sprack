package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import spray.http.{MediaTypes, HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._


class HeaderSpec extends WordSpec with MustMatchers with SprackSpec {

  "Headers" must {
    "be filtered" in {
      RackApp.filterHeaders(List(`Content-Type`(`application/json`), `Content-Length`(10))).isEmpty must be(true)
    }

    "be sent to the rack app" in {
      val app = rackApp("src/test/resources/headers.ru", 80)
      val resp = app.call(HttpRequest(GET, Uri("/"), entity = HttpEntity("foo"), headers = List(`Accept`(MediaTypes.`application/json`)))).right.get
      resp.status.intValue must be(200)
    }
  }

}

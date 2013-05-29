package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{HttpEntity, Uri, HttpRequest}
import spray.http.HttpMethods._
import spray.http.ContentType.`application/json`


class ChunkedSpec extends WordSpec with MustMatchers {


  "Chunked" must {
    "do chunks" in {
      try {
        val app = new RackApp("src/test/resources/chunked.ru")
        val (resp, chunks) = app.call(HttpRequest(GET, Uri("/"))).left.get
        resp.status.value must equal(200)
        chunks.size must equal("Hello".size)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }
  }

}

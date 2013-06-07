package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.{Uri, HttpRequest}
import spray.http.HttpMethods._


class ChunkedSpec extends WordSpec with MustMatchers {


  "Chunked" must {
    "do chunks" in {
      try {
        val app = new RackApp("src/test/resources/chunked.ru", 80)
        val (resp, chunks) = app.call(HttpRequest(GET, Uri("/"))).left.get
        resp.status.intValue must equal(200)
        chunks.size must equal("Hello".size)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          fail(e)
      }
    }
  }

}

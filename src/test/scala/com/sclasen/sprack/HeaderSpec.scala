package com.sclasen.sprack

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import spray.http.HttpHeaders._
import spray.http.ContentTypes._


class HeaderSpec extends WordSpec with MustMatchers {

  "Headers" must {
    "be filtered" in {
      RackApp.filterHeaders(List(`Content-Type`(`application/json`), `Content-Length`(10))).isEmpty must be(true)
    }
  }

}

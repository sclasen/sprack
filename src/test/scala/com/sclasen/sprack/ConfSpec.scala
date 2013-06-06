package com.sclasen.sprack

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import akka.actor.ActorSystem


class ConfSpec extends WordSpec with MustMatchers {

  "Default Config" must {
    "be overridden" in {
      val sys = ActorSystem()
      val d = sys.dispatchers.lookup("sprack.rack-dispatcher")
      d must not equal (sys.dispatcher)
      d.prerequisites.settings.config.getInt("sprack.rack-dispatcher.fork-join-executor.parallelism-max") must equal(1)
    }

  }
}

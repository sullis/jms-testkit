package jmstestkit

import org.scalatest.{Matchers, WordSpec}

class JmsBrokerSpec extends WordSpec with Matchers {
  "restart" should {
    "restart an already started broker" in {
      val broker = JmsBroker()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "restart a stopped broker" in {
      val broker = JmsBroker()
      broker.stop()
      broker.isStarted shouldBe false
      broker.isStopped shouldBe true
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "restart multiple times" in {
      val broker = JmsBroker()
      broker.restart()
      broker.restart()
      broker.restart()
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }
  }
}

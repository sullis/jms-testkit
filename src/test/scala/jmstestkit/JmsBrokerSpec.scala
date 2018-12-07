package jmstestkit

import org.scalatest.{Matchers, WordSpec}

class JmsBrokerSpec extends WordSpec with Matchers {
  "restart" should {
    "restart an already started broker" in {
      val broker = JmsBroker()
      broker.isStarted shouldBe true
      broker.restart()
      Thread.sleep(150)
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "restart a stopped broker" in {
      val broker = JmsBroker()
      broker.stop()
      broker.isStarted shouldBe false
      broker.restart()
      Thread.sleep(150)
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }
  }
}

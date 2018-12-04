package jmstestkit

import org.scalatest.{WordSpec, Matchers}

class JmsBrokerSpec extends WordSpec with Matchers {

  "JmsBroker" should {
    "support JMS queues" in {
      val broker = JmsBrokerBuilder.buildInMemoryBroker()
      broker.queueSize shouldBe 0
      broker.publishMessage("a1a")
      broker.queueSize shouldBe 1
      broker.publishMessage("b2b")
      broker.browseQueue shouldBe Seq("a1a", "b2b")
      broker.queueSize shouldBe 2
      broker.publishMessage("c3c")
      // first check
      broker.queueSize shouldBe 3
      broker.browseQueue shouldBe Seq("a1a", "b2b", "c3c")
      // second check
      broker.queueSize shouldBe 3
      broker.browseQueue shouldBe Seq("a1a", "b2b", "c3c")
    }
  }
}

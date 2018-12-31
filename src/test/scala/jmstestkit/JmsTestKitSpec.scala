package jmstestkit

import org.scalatest.{Matchers, WordSpec}

class JmsTestKitSpec
  extends WordSpec
  with Matchers
  with JmsTestKit {

  def connectionFactorySanityCheck: Unit = {
    withConnectionFactory() { cf =>
      val conn = cf.createConnection
      conn.start()
      conn.stop()
      conn.close()
    }
  }

  def brokerSanityCheck: Unit = {
    withBroker() { broker =>
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
      val cf = broker.createConnectionFactory
      val conn = cf.createConnection
      conn.start()
      conn.stop()
      conn.close()
    }
  }

  def queueSanityCheck: Unit = {
    withQueue() { queue =>
      queue.size shouldBe 0
      queue.publishMessage("Hello world")
      queue.size shouldBe 1
      val broker = queue.broker
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
      val cf = broker.createConnectionFactory
      val conn = cf.createConnection
      conn.start()
      conn.stop()
      conn.close()
    }
  }
}

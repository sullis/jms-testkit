package jmstestkit

import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class JmsTestKitSpec
  extends AnyWordSpec
  with Matchers
  with JmsTestKit {

  "JmsTestKit " should {
    "withConnectionFactory happy path" in {
      withConnectionFactory() { cf =>
        val conn = cf.createConnection
        conn.start()
        conn.stop()
        conn.close()
      }
    }

    "withBroker happy path" in {
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

    "withQueue" in {
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

    "withTopic" in {
      withTopic() { topic =>
        topic.toSeq.size shouldBe 0
        topic.publishMessage("Hello world")

        eventually {
          topic.toSeq.size shouldBe 1
        }

        val broker = topic.broker
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
}

package jmstestkit

import org.scalatest.{WordSpec, Matchers}
import com.google.common.collect.Lists

class JmsQueueSpec extends WordSpec with Matchers {

  "JmsQueue" should {
    "support queue operations" in {
      val queue = JmsQueue()
      queue.size shouldBe 0
      queue.publishMessage("a1a")
      queue.size shouldBe 1
      queue.publishMessage("b2b")
      queue.toSeq shouldBe Seq("a1a", "b2b")
      queue.size shouldBe 2
      queue.publishMessage("c3c")
      // first check
      queue.size shouldBe 3
      queue.toSeq shouldBe Seq("a1a", "b2b", "c3c")
      // second check
      queue.size shouldBe 3
      queue.toSeq shouldBe Seq("a1a", "b2b", "c3c")
    }

    "toJavaList sanity check" in {
      val queue = JmsQueue()
      queue.toJavaList.isEmpty shouldBe true
      queue.publishMessage("Portland")
      queue.toJavaList should equal (Lists.newArrayList("Portland"))
      queue.publishMessage("Seattle")
      queue.publishMessage("Eugene")
      queue.toJavaList should equal (Lists.newArrayList("Portland", "Seattle", "Eugene"))
    }

    "createConnectionFactory sanity check " in {
      val queue = JmsQueue()
      queue.publishMessage("Hello world")
      val connFactory = queue.createConnectionFactory
      connFactory.getBrokerURL should not be (null)
      val conn = connFactory.createConnection()
      conn.getMetaData should not be (null)
      val ackMode = javax.jms.Session.SESSION_TRANSACTED
      val session = conn.createSession(true, ackMode)
      session.getTransacted shouldBe (true)
      session.getAcknowledgeMode shouldBe (ackMode)
      val q = session.createQueue(queue.queueName)
      val textMessage = session.createTextMessage("Hello world")
      session.close()
      conn.close()
    }

    "two queues" in {
      val queue1 = JmsQueue()
      val queue2 = JmsQueue()
      queue1.queueName should not be (queue2.queueName)
      queue1.defaultUriString should not be (queue2.defaultUriString)
      queue1.publishMessage("California")
      queue1.size shouldBe 1
      queue2.size shouldBe 0
      queue2.publishMessage("New York")
      queue1.toSeq shouldBe (Seq("California"))
      queue2.toSeq shouldBe (Seq("New York"))
    }
  }
}

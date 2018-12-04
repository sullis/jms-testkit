package jmstestkit

import org.scalatest.{WordSpec, Matchers}
import com.google.common.collect.Lists

class JmsQueueSpec extends WordSpec with Matchers {

  "JmsQueue" should {
    "support queue operations" in {
      val queue = JmsQueueBuilder.build()
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
      val queue = JmsQueueBuilder.build()
      queue.toJavaList.isEmpty shouldBe true
      queue.publishMessage("Portland")
      queue.toJavaList should equal (Lists.newArrayList("Portland"))
      queue.publishMessage("Seattle")
      queue.publishMessage("Eugene")
      queue.toJavaList should equal (Lists.newArrayList("Portland", "Seattle", "Eugene"))
    }

    "createConnectionFactory sanity check " in {
      val queue = JmsQueueBuilder.build()
      queue.publishMessage("Hello world")
      val connFactory = queue.createConnectionFactory
      connFactory.getBrokerURL should not be (null)
      val conn = connFactory.createConnection()
      conn.getMetaData should not be (null)
      val session = conn.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE)
      session.getTransacted shouldBe (true)
      session.close()
      conn.close()
    }
  }
}

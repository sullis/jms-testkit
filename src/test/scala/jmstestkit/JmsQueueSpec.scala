package jmstestkit

import org.scalatest.{Matchers, WordSpec}
import com.google.common.collect.Lists
import javax.jms.JMSException

class JmsQueueSpec extends WordSpec with Matchers {

  "JmsQueue" should {
    "valid queue name" in {
      val queue = JmsQueue()
      queue.queueName.size shouldBe > (0)
    }

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

    "stop() sanity check" in {
      val queue = JmsQueue()
      val connFactory = queue.createQueueConnectionFactory
      val qconn = connFactory.createQueueConnection
      val qsession = qconn.createQueueSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE)
      val q = qsession.createQueue(queue.queueName)
      val sender = qsession.createSender(q)
      val msg = qsession.createTextMessage("abcdef")

      queue.stop()
      Thread.sleep(1000)

      intercept[JMSException] { connFactory.createQueueConnection }.getMessage should startWith ("Could not create Transport")
      intercept[JMSException] { qsession.createSender(q) }.getMessage should startWith ("The Session is closed")
      intercept[JMSException] { sender.send(msg) }.getMessage should startWith ("The producer is closed")

      intercept[IllegalStateException] { queue.toSeq }.getMessage shouldBe "Broker is stopped"
      intercept[IllegalStateException] { queue.toJavaList }.getMessage shouldBe "Broker is stopped"
      intercept[IllegalStateException] { queue.createQueueConnectionFactory }.getMessage shouldBe "Broker is stopped"
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

    "createQueueConnectionFactory sanity check " in {
      val queue = JmsQueue()
      queue.publishMessage("Hello world")
      val connFactory = queue.createQueueConnectionFactory
      val conn = connFactory.createConnection()
      conn.getMetaData should not be (null)
      val ackMode = javax.jms.Session.SESSION_TRANSACTED
      val session = conn.createSession(true, ackMode)
      session.getTransacted shouldBe (true)
      session.getAcknowledgeMode shouldBe (ackMode)
      val q = session.createQueue(queue.queueName)
      val textMessage = session.createTextMessage("Excellent message")
      val producer = session.createProducer(q)
      producer.send(textMessage)
      session.commit()
      session.close()
      conn.close()
      queue.toSeq shouldBe Seq("Hello world", "Excellent message")
    }

    "two queues" in {
      val queue1 = JmsQueue()
      val queue2 = JmsQueue()
      queue1.queueName should not be (queue2.queueName)
      queue1.broker.brokerUri should not be (queue2.broker.brokerUri)
      queue1.publishMessage("California")
      queue1.size shouldBe 1
      queue2.size shouldBe 0
      queue2.publishMessage("New York")
      queue1.toSeq shouldBe (Seq("California"))
      queue2.toSeq shouldBe (Seq("New York"))
      queue1.stop()
      queue2.toSeq shouldBe (Seq("New York"))
    }
  }
}

package jmstestkit

import com.google.common.collect.Lists
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import jakarta.jms.JMSException

class JmsTopicSpec extends AnyWordSpec with Matchers {

  "JmsTopic" should {
    "valid topic name" in {
      val topic = JmsTopic()
      topic.topicName.size shouldBe > (0)
      topic.broker.stop()
    }

    "toSeq returns correct values" in {
      val topic = JmsTopic()
      topic.publishMessage("message1")
      topic.publishMessage("message2")

      eventually {
        topic.toSeq shouldBe Seq("message1", "message2")
      }

      topic.broker.stop()
    }

    "toJavaList returns correct values" in {
      val topic = JmsTopic()
      topic.publishMessage("jMessage1")
      topic.publishMessage("jMessage2")

      eventually {
        topic.toJavaList shouldBe (Lists.newArrayList("jMessage1", "jMessage2"))
      }

      topic.broker.stop()
    }

    "stop() sanity check" in {
      val topic = JmsTopic()
      val connFactory = topic.createTopicConnectionFactory
      val conn = connFactory.createTopicConnection
      val tsession = conn.createTopicSession(true, jakarta.jms.Session.AUTO_ACKNOWLEDGE)
      val t = tsession.createTopic(topic.topicName)
      val sender = tsession.createPublisher(t)
      val msg = tsession.createTextMessage("abcdef")

      topic.broker.stop()
      Thread.sleep(1000)

      topic.broker.isStarted shouldBe (false)
      topic.broker.isStopped shouldBe (true)

      intercept[JMSException] { connFactory.createTopicConnection }.getMessage should startWith ("Could not create Transport")
      intercept[JMSException] { tsession.createPublisher(t) }.getMessage should startWith ("The Session is closed")
      intercept[JMSException] { sender.send(msg) }.getMessage should startWith ("The producer is closed")

      intercept[IllegalStateException] { topic.createTopicConnectionFactory }.getMessage shouldBe "Broker is stopped"
    }

    "createTopicConnectionFactory sanity check " in {
      val topic = JmsTopic()
      topic.publishMessage("Hello world")
      val connFactory = topic.createTopicConnectionFactory
      val conn = connFactory.createConnection()
      conn.getMetaData should not be (null)
      val ackMode = jakarta.jms.Session.SESSION_TRANSACTED
      val session = conn.createSession(true, ackMode)
      session.getTransacted shouldBe (true)
      session.getAcknowledgeMode shouldBe (ackMode)
      val t = session.createTopic(topic.topicName)
      val textMessage = session.createTextMessage("Excellent message")
      val producer = session.createProducer(t)
      producer.send(textMessage)
      session.commit()
      producer.send(session.createTextMessage("Please rollback this message."))
      session.rollback()
      session.close()
      conn.close()
    }

    "two topics" in {
      val topic1 = JmsTopic()
      val topic2 = JmsTopic()
      topic1.topicName should not be (topic2.topicName)

      topic1.broker.brokerUri should not be (topic2.broker.brokerUri)
      topic1.broker.hashCode should not be (topic2.broker.hashCode)

      topic1.publishMessage("California")
      topic2.publishMessage("New York")

      eventually {
        topic1.toSeq shouldBe Seq("California")
        topic2.toSeq shouldBe Seq("New York")
      }

      topic1.broker.stop()
      topic2.broker.stop()
    }
  }
}

package jmstestkit

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import javax.jms.JMSException

class JmsTopicSpec extends AnyWordSpec with Matchers {

  "JmsTopic" should {
    "valid topic name" in {
      val topic = JmsTopic()
      topic.topicName.size shouldBe > (0)
    }

    "stop() sanity check" in {
      val topic = JmsTopic()
      val connFactory = topic.createTopicConnectionFactory
      val conn = connFactory.createTopicConnection
      val tsession = conn.createTopicSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE)
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
      val ackMode = javax.jms.Session.SESSION_TRANSACTED
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
      topic1.broker.stop()
      topic2.broker.stop()
    }
  }
}

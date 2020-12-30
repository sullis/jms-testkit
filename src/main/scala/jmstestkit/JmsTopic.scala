package jmstestkit

import java.util.UUID

import javax.jms.{ConnectionFactory, TopicConnectionFactory}

import scala.util.Try

class JmsTopic(val broker: JmsBroker) {

  val topicName: String = "Topic-" + UUID.randomUUID.toString

  def createTopicConnectionFactory: TopicConnectionFactory = broker.createTopicConnectionFactory
  def createConnectionFactory: ConnectionFactory = broker.createConnectionFactory

  def publishMessage(msg: String): Unit = {
    val conn = createTopicConnectionFactory.createTopicConnection()
    val session = conn.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val topic = session.createTopic(topicName)
    val publisher = session.createPublisher(topic)
    publisher.send(session.createTextMessage(msg))
    Try { publisher.close() }
    Try { session.close() }
    Try { conn.close() }
  }

  override def toString(): String = {
    getClass.getSimpleName + s"[${topicName}]"
  }
}

object JmsTopic {
  def apply(): JmsTopic = {
    new JmsTopic(JmsBroker())
  }
}


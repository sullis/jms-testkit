package jmstestkit

import java.util.UUID

import javax.jms.{QueueConnectionFactory, ConnectionFactory, TextMessage}

import scala.collection.JavaConverters._

class JmsQueue(val broker: JmsBroker) {

  def isStarted(): Boolean = broker.isStarted
  val queueName: String = "Queue-" + UUID.randomUUID.toString

  def size: Long = calculateQueueSize(queueName)

  def createQueueConnectionFactory: QueueConnectionFactory = broker.createQueueConnectionFactory
  def createConnectionFactory: ConnectionFactory = broker.createConnectionFactory

  private def calculateQueueSize(qName: String): Long = {
    import scala.collection.JavaConverters._
    val destinationMap = broker.service.getRegionBroker.getDestinationMap().asScala
    val dests = destinationMap.values.filter(_.getName == qName)
    val counts = dests.map {
      _.getDestinationStatistics.getMessages.getCount
    }
    counts.sum
  }

  def toSeq(): Seq[String] = {
    val qconn = createQueueConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    qconn.start
    session.createBrowser(queue).getEnumeration.asScala.toSeq.asInstanceOf[Seq[TextMessage]].map(_.getText)
  }

  def toJavaList(): java.util.List[String] = {
    java.util.Collections.unmodifiableList(toSeq.asJava)
  }

  def publishMessage(msg: String): Unit = {
    val qconn = createQueueConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    val sender = session.createSender(queue)
    sender.send(session.createTextMessage(msg))
  }

  def stop(): Unit = {
    broker.stop()
  }
}

object JmsQueue {
  def apply(): JmsQueue = {
    new JmsQueue(JmsBroker())
  }
}


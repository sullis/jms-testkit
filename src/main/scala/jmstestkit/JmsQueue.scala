package jmstestkit

import java.util.{Collections, UUID}

import javax.jms.{ConnectionFactory, QueueConnectionFactory, TextMessage}

import scala.collection.JavaConverters._
import scala.util.Try

class JmsQueue(val broker: JmsBroker) {

  val queueName: String = "Queue-" + UUID.randomUUID.toString

  def size: Long = calculateQueueSize(queueName)

  def createQueueConnectionFactory: QueueConnectionFactory = broker.createQueueConnectionFactory
  def createConnectionFactory: ConnectionFactory = broker.createConnectionFactory

  private def calculateQueueSize(qName: String): Long = {
    import scala.collection.JavaConverters._
    /*
    val destinationMap = broker.service.getRegionBroker.getDestinationMap().asScala
    val dests = destinationMap.values.filter(_.getName == qName)
    val counts = dests.map {
      _.getDestinationStatistics.getMessages.getCount
    }
    counts.sum
     */
    0
  }

  def toSeq: Seq[String] = {
    val qconn = createQueueConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    qconn.start
    val browser = session.createBrowser(queue)
    val result = Collections.list(browser.getEnumeration).asScala.asInstanceOf[Iterable[TextMessage]].map(_.getText).toSeq
    Try { browser.close() }
    Try { session.close() }
    Try { qconn.close() }
    result
  }

  def toJavaList: java.util.List[String] = {
    java.util.Collections.unmodifiableList(toSeq.asJava)
  }

  def publishMessage(msg: String): Unit = {
    val qconn = createQueueConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    val sender = session.createSender(queue)
    sender.send(session.createTextMessage(msg))
    Try { sender.close() }
    Try { session.close() }
    Try { qconn.close() }
  }

  override def toString(): String = {
    getClass.getSimpleName + s"[${queueName}]"
  }
}

object JmsQueue {
  def apply(): JmsQueue = {
    new JmsQueue(JmsBroker())
  }
}


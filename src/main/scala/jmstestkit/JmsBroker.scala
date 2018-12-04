package jmstestkit

import org.apache.activemq.broker.{BrokerFactory, BrokerService}
import org.apache.activemq.ActiveMQConnectionFactory
import java.net.URI
import java.util.UUID
import javax.jms.TextMessage

object JmsBrokerBuilder {
  def buildInMemoryBroker(): JmsBroker = {
    val inMemoryBrokerName = "brokerName-" + UUID.randomUUID.toString
    val transportUri = s"vm://${inMemoryBrokerName}?create=false"
    val brokerConfigUri = new URI(s"broker:(${transportUri})/${inMemoryBrokerName}?persistent=false&useJmx=false")

    val brokerService = BrokerFactory.createBroker(brokerConfigUri)
    brokerService.setPersistent(false)
    brokerService.setUseJmx(false)
    brokerService.setStartAsync(false)
    brokerService.start()
    new JmsBroker(brokerService)
  }
}

class JmsBroker(service: BrokerService) {

  def isStarted(): Boolean = service.isStarted
  def brokerName: String = service.getBrokerName
  val queueName: String = "Queue-" + UUID.randomUUID.toString

  def queueSize: Long = calculateQueueSize(queueName)

  private def calculateQueueSize(qName: String): Long = {
    import scala.collection.JavaConverters._
    val destinationMap = service.getRegionBroker.getDestinationMap().asScala
    val dests = destinationMap.values.filter(_.getName == qName)
    val counts = dests.map {
      _.getDestinationStatistics.getMessages.getCount
    }
    counts.sum
  }

  def defaultUriString: String = service.getDefaultSocketURIString

  def createConnectionFactory: ActiveMQConnectionFactory = {
    val connFactory = new ActiveMQConnectionFactory(service.getDefaultSocketURIString)
    connFactory.setUseCompression(false)
    connFactory
  }

  def browseQueue(): Seq[String] = {
    import scala.collection.JavaConverters._
    val qconn = createConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    qconn.start
    session.createBrowser(queue).getEnumeration.asScala.toSeq.asInstanceOf[Seq[TextMessage]].map(_.getText)
  }

  def publishMessage(msg: String): Unit = {
    val qconn = createConnectionFactory.createQueueConnection()
    val session = qconn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)
    val queue = session.createQueue(queueName)
    val sender = session.createSender(queue)
    sender.send(session.createTextMessage(msg))
  }
}

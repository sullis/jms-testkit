package jmstestkit

import java.util.UUID
import javax.naming.Context
import org.apache.activemq.artemis.jms.client.{ActiveMQConnectionFactory, ActiveMQQueueConnectionFactory, ActiveMQTopicConnectionFactory}
import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory
import org.apache.activemq.artemis.core.server.ActiveMQServer
import org.apache.activemq.artemis.core.server.ActiveMQServers
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl

import java.util.concurrent.TimeUnit
import scala.util.Try

class JmsBroker(val service: ActiveMQServer) {
  checkState()

  def isStarted: Boolean = service.isStarted
  def isStopped: Boolean = !isStarted

  def brokerUri: String = {
    val connMap = service.getConnectorsService.getConnectors
    val keys = connMap.keySet()
    keys.iterator().next()

  }

  def closeClientConnections(): Unit = {
    /*
    for (clientConn <- service.getBroker.getClients) {
      Try { clientConn.stop() }
    }
     */
  }

  def clientConnectionCount: Int = service.getConnectionCount

  def createQueueConnectionFactory: jakarta.jms.QueueConnectionFactory = {
    checkState()
    new ActiveMQQueueConnectionFactory(brokerUri)
  }

  def createTopicConnectionFactory: jakarta.jms.TopicConnectionFactory = {
    checkState()
    new ActiveMQTopicConnectionFactory(brokerUri)
  }

  def createConnectionFactory: jakarta.jms.ConnectionFactory = {
    checkState()
    new ActiveMQConnectionFactory(brokerUri)
  }

  def createJndiEnvironment: java.util.Hashtable[String, String] = {
    import scala.collection.JavaConverters._
    val env = new java.util.Hashtable[String, String]()
    env.put(Context.PROVIDER_URL, brokerUri)
    env.put(Context.INITIAL_CONTEXT_FACTORY, classOf[ActiveMQInitialContextFactory].getName)
    /*
    val destinations = service.getBroker.getDurableDestinations.asScala
    for (dest <- destinations) {
      val name = dest.getPhysicalName
      if (dest.isQueue) {
        env.put("queue." + name, name)
      }
      else if (dest.isTopic) {
        env.put("topic." + name, name)
      }
    }
     */
    env
  }

  def createJndiContext: javax.naming.Context = {
    val factory = new ActiveMQInitialContextFactory
    factory.getInitialContext(createJndiEnvironment)
  }

  def start(force: Boolean = true): Unit = {
    service.start()
    service.waitForActivation(3, TimeUnit.SECONDS)
  }

  def restart(): Unit = {
    stop()
    start(force = true)
  }

  def stop(): Unit = service.stop()

  override def toString(): String = {
    getClass.getSimpleName + s"[${brokerUri}]"
  }

  private def checkState(): Unit = {
    if (!service.isStarted) {
      throw new IllegalStateException("Broker is stopped")
    }
  }
}

object JmsBroker {
  def apply(): JmsBroker = {
    val inMemoryBrokerName = "brokerName-" + UUID.randomUUID.toString

    val config = new ConfigurationImpl()
      .setName(inMemoryBrokerName)
      .setJMXManagementEnabled(false)
      .setPersistenceEnabled(false)
      .setSecurityEnabled(false)
      .addAcceptorConfiguration("invm", "vm://0")

    val server = ActiveMQServers.newActiveMQServer(config)
    server.start()
    server.waitForActivation(5, TimeUnit.SECONDS)

    val connectors = server.getConnectorsService.getConnectors
    System.out.println("connectors: " + connectors)

    new JmsBroker(server)
  }
}

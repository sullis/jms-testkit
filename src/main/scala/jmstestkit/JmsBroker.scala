package jmstestkit

import org.apache.activemq.artemis.core.config.Configuration
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl
import org.apache.activemq.artemis.core.server.{ActiveMQServer, ActiveMQServers}
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import scala.jdk.CollectionConverters._

import java.net.URI
import java.util.UUID
import javax.naming.Context
import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory

import java.util.concurrent.TimeUnit
import scala.util.Try

class JmsBroker(val server: ActiveMQServer) {
  checkState()

  def isStarted: Boolean = server.isStarted
  def isStopped: Boolean = (!server.isStarted) && (!server.isActive)

  def brokerUri: String = "todo: fixme"

  def closeClientConnections(): Unit = {
    for (conn <- server.getBrokerConnections.asScala) {
      Try { conn.stop() }
    }
  }

  def clientConnectionCount: Int = server.getBrokerConnections.size()

  def createQueueConnectionFactory: jakarta.jms.QueueConnectionFactory = {
    checkState()
    new ActiveMQConnectionFactory(/* fixme? */)
  }

  def createTopicConnectionFactory: jakarta.jms.TopicConnectionFactory = {
    checkState()
    new ActiveMQConnectionFactory(/* fixme ? */)
  }

  def createConnectionFactory: jakarta.jms.ConnectionFactory = {
    checkState()
    new ActiveMQConnectionFactory(/* fixme ? */)
  }

  def createJndiEnvironment: java.util.Hashtable[String, String] = {
    val env = new java.util.Hashtable[String, String]()
    env.put(Context.PROVIDER_URL, brokerUri)
    env.put(Context.INITIAL_CONTEXT_FACTORY, classOf[ActiveMQInitialContextFactory].getName)
    val destinations = server.getBroker.getDurableDestinations.asScala
    for (dest <- destinations) {
      val name = dest.getPhysicalName
      if (dest.isQueue) {
        env.put("queue." + name, name)
      }
      else if (dest.isTopic) {
        env.put("topic." + name, name)
      }
    }
    env
  }

  def createJndiContext: javax.naming.Context = {
    val factory = new ActiveMQInitialContextFactory
    factory.getInitialContext(createJndiEnvironment)
  }

  def start(force: Boolean = true): Unit = server.start(force)

  def restart(): Unit = {
    stop()
    start(force = true)
  }

  def stop(): Unit = server.stop()

  override def toString(): String = {
    getClass.getSimpleName + s"[${brokerUri}]"
  }

  private def checkState(): Unit = {
    if (server.isStopped) {
      throw new IllegalStateException("Broker is stopped")
    } else if (server.isStopping) {
      throw new IllegalStateException("Broker is stopping")
    }
  }
}

object JmsBroker {
  def apply(): JmsBroker = {
    val inMemoryBrokerName = "brokerName-" + UUID.randomUUID.toString
    val transportUri = s"vm://${inMemoryBrokerName}?create=false"
    val brokerConfigUri = new URI(s"broker:(${transportUri})/${inMemoryBrokerName}?persistent=false&useJmx=false")

    val config = new ConfigurationImpl()
    config.setName(inMemoryBrokerName)
    config.setJMXManagementEnabled(false)
    val activeMqServer = ActiveMQServers.newActiveMQServer(config, false)
    activeMqServer.start()
    activeMqServer.waitForActivation(5, TimeUnit.SECONDS)
    new JmsBroker(activeMqServer)
  }
}

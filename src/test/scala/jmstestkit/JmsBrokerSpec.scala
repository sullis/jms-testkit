package jmstestkit

import javax.naming.Context
import org.apache.activemq.jndi.ActiveMQInitialContextFactory
import org.scalatest.{Matchers, WordSpec}

class JmsBrokerSpec extends WordSpec with Matchers {
  "restart" should {
    "restart an already started broker" in {
      val broker = JmsBroker()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "restart a stopped broker" in {
      val broker = JmsBroker()
      broker.stop()
      broker.isStarted shouldBe false
      broker.isStopped shouldBe true
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "restart multiple times" in {
      val broker = JmsBroker()
      broker.restart()
      broker.restart()
      broker.restart()
      broker.restart()
      broker.isStarted shouldBe true
      broker.isStopped shouldBe false
    }

    "clientConnectionCount" in {
      val broker = JmsBroker()
      broker.clientConnectionCount shouldBe 0
      val connFactory = broker.createConnectionFactory
      val conn1 = connFactory.createConnection()
      val conn2 = connFactory.createConnection()
      conn1.start()
      broker.clientConnectionCount shouldBe 1
      conn2.start()
      broker.clientConnectionCount shouldBe 2
      conn1.close()
      broker.clientConnectionCount shouldBe 1
      conn2.close()
      broker.clientConnectionCount shouldBe 0
    }

    "closeClientConnections" in {
      val broker = JmsBroker()
      broker.clientConnectionCount shouldBe 0
      val connFactory = broker.createConnectionFactory
      val conn = connFactory.createConnection()
      conn.start()
      broker.clientConnectionCount shouldBe 1
      broker.closeClientConnections()
      broker.clientConnectionCount shouldBe 0
      conn.close()
      broker.clientConnectionCount shouldBe 0
    }

    "createJndiContext" in {
      val queue = JmsQueue()
      val broker = queue.broker
      queue.publishMessage("Hello world!")
      val ctx = broker.createJndiContext

      val jndiQueue = ctx.lookup(queue.queueName).asInstanceOf[javax.jms.Queue]
      jndiQueue.getQueueName shouldBe queue.queueName

      intercept[javax.naming.NameNotFoundException] { ctx.lookup("bogusThing") }.getMessage shouldBe ("bogusThing")

      ctx.close()
    }

    "createJndiEnvironment" in {
      val queue = JmsQueue()
      val broker = queue.broker
      queue.publishMessage("Hello world!")
      val env = broker.createJndiEnvironment
      env.get(s"queue.${queue.queueName}") shouldBe queue.queueName
      env.get(Context.PROVIDER_URL) shouldBe broker.brokerUri
      env.get(Context.INITIAL_CONTEXT_FACTORY) shouldBe classOf[ActiveMQInitialContextFactory].getName
    }
  }
}

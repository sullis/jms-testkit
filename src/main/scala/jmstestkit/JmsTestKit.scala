package jmstestkit

trait JmsTestKit {
  def withBroker()(test: JmsBroker => Unit): Unit = {
    val broker = JmsBroker()
    try {
      test(broker)
      Thread.sleep(500)
    } finally {
      broker.stop()
    }
  }

  def withConnectionFactory()(test: javax.jms.ConnectionFactory => Unit): Unit = {
    withBroker() { broker =>
      test(broker.createConnectionFactory)
    }
  }

  def withQueue()(test: JmsQueue => Unit): Unit = {
    val queue = JmsQueue()
    val broker = queue.broker
    try {
      test(queue)
      Thread.sleep(500)
    } finally {
      broker.stop()
    }
  }

}

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

  def withConnectionFactory()(test: jakarta.jms.ConnectionFactory => Unit): Unit = {
    withBroker() { broker =>
      test(broker.createConnectionFactory)
    }
  }

  def withQueue()(test: JmsQueue => Unit): Unit = {
    val queue = JmsQueue()
    try {
      test(queue)
      Thread.sleep(500)
    } finally {
      queue.broker.stop()
    }
  }

  def withTopic()(test: JmsTopic => Unit): Unit = {
    val topic = JmsTopic()
    try {
      test(topic)
      Thread.sleep(500)
    } finally {
      topic.broker.stop()
    }
  }

}

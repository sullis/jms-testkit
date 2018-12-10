# jms-testkit
in-memory JMS library


# Scala build.sbt

```

"io.github.sullis" %% "jms-testkit" % "0.0.8" % Test


```


# Scala code example

```

import jmstestkit.JmsQueue

val queue = JmsQueue()

queue.publishMessage("Portland")
queue.publishMessage("Seattle")

System.out.println("queueName: " + queue.queueName)
System.out.println("size: " + queue.size)
System.out.println("snapshot: " + queue.toSeq)

val connFactory = queue.createQueueConnectionFactory // javax.jms.QueueConnectionFactory

```

# Java code example

```

import jmstestkit.JmsQueue;
import javax.jms.QueueConnectionFactory;

JmsQueue queue = JmsQueue.apply();

queue.publishMessage("Whistler");
queue.publishMessage("Blackcomb");

System.out.println("queueName: " + queue.queueName());
System.out.println("size: " + queue.size());
System.out.println("snapshot: " + queue.toJavaList());

QueueConnectionFactory connFactory = queue.createQueueConnectionFactory(); // javax.jms.QueueConnectionFactory

```

# Related resources

- [https://github.com/apache/activemq/blob/master/activemq-tooling/activemq-junit/src/main/java/org/apache/activemq/junit/EmbeddedActiveMQBroker.java]

- [https://github.com/apache/camel/blob/master/components/camel-sjms/src/test/java/org/apache/camel/component/sjms/batch/EmbeddedActiveMQBroker.java]

- [https://github.com/apache/activemq-artemis/blob/master/artemis-junit/src/main/java/org/apache/activemq/artemis/junit/EmbeddedJMSResource.java]

- [https://github.com/jkorab/camel-jms-batch/blob/master/src/test/java/org/apache/camel/component/sjms/batch/EmbeddedActiveMQBroker.java]

- [http://activemq.apache.org/how-to-unit-test-jms-code.html]

- [https://stackoverflow.com/questions/2870431/unit-testing-with-jms-activemq]

- [https://stackoverflow.com/questions/14342430/are-there-any-mq-servers-that-can-run-embedded-in-a-java-process]


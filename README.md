# jms-testkit
in-memory JMS library


# Scala build.sbt

```

"io.github.sullis" %% "jms-testkit" % "0.2.8" % Test


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

# Maven pom.xml

```
<dependency>
    <groupId>io.github.sullis</groupId>
    <artifactId>jms-testkit_2.12</artifactId>
    <version>0.2.8</version>
    <scope>test</scope>
</dependency>

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

QueueConnectionFactory connFactory = queue.createQueueConnectionFactory();

```
# Projects that use [jms-testkit]

- [fs2-jms](https://github.com/kiambogo/fs2-jms)
- [Alpakka](https://github.com/akka/alpakka/tree/master/jms/src)

# Related resources

- [http://activemq.apache.org/how-to-unit-test-jms-code.html]

- [https://stackoverflow.com/questions/2870431/unit-testing-with-jms-activemq]

- [https://stackoverflow.com/questions/14342430/are-there-any-mq-servers-that-can-run-embedded-in-a-java-process]

- [https://github.com/apache/activemq/blob/master/activemq-tooling/activemq-junit/src/main/java/org/apache/activemq/junit/EmbeddedActiveMQBroker.java]

- [https://github.com/apache/camel/blob/master/components/camel-sjms/src/test/java/org/apache/camel/component/sjms/batch/EmbeddedActiveMQBroker.java]

- [https://github.com/apache/activemq-artemis/blob/master/artemis-junit/src/main/java/org/apache/activemq/artemis/junit/EmbeddedJMSResource.java]

- [https://github.com/jkorab/camel-jms-batch/blob/master/src/test/java/org/apache/camel/component/sjms/batch/EmbeddedActiveMQBroker.java]

- mockrunner-jms [http://mockrunner.github.io/mockrunner/examplesjms.html]

# Acknowledgements

This project uses [sbt-ci-release](https://github.com/olafurpg/sbt-ci-release)


# jms-testkit
in-memory JMS library


# Scala build.sbt

```

"io.github.sullis" %% "jms-testkit" % "0.0.5" % Test


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

val connFactory = queue.createConnectionFactory // javax.jms.ConnectionFactory

```

# Java code example

```

import jmstestkit.JmsQueue;
import javax.jms.ConnectionFactory;

JmsQueue queue = JmsQueue.apply();

queue.publishMessage("Whistler");
queue.publishMessage("Blackcomb");

System.out.println("queueName: " + queue.queueName());
System.out.println("size: " + queue.size());
System.out.println("snapshot: " + queue.toJavaList());

ConnectionFactory connFactory = queue.createConnectionFactory(); // javax.jms.ConnectionFactory

```


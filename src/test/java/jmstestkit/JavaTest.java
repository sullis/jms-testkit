
package jmstestkit;

import com.google.common.collect.Lists;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class JavaTest {

    @Test
    public void brokerSanityCheck()  {
        JmsBroker broker1 = JmsBroker.apply();
        JmsBroker broker2 = JmsBroker.apply();
        assertNotEquals(broker1.brokerUri(), broker2.brokerUri());
        assertTrue(broker1.isStarted());
        assertTrue(broker2.isStarted());
        broker1.stop();
        broker2.stop();
        assertTrue(broker1.isStopped());
        assertTrue(broker2.isStopped());
    }

    @Test
    public void queueSanityCheck()  {
        JmsQueue queue = JmsQueue.apply();
        queue.publishMessage("Hello");
        queue.publishMessage("Bonjour");
        assertNotNull(queue.queueName());
        assertNotNull(queue.createQueueConnectionFactory());
        assertEquals(queue.size(), 2);
        assertEquals(queue.toJavaList(), Lists.newArrayList("Hello", "Bonjour"));
    }
}


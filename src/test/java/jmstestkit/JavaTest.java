
package jmstestkit;

import com.google.common.collect.Lists;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class JavaTest {

    @Test
    public void sanityCheck()  {
        JmsQueue queue = JmsQueue.apply();
        queue.publishMessage("Hello");
        queue.publishMessage("Bonjour");
        assertNotNull(queue.queueName());
        assertNotNull(queue.createQueueConnectionFactory());
        assertEquals(queue.size(), 2);
        assertEquals(queue.toJavaList(), Lists.newArrayList("Hello", "Bonjour"));
    }
}


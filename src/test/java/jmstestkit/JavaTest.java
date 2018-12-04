
package jmstestkit;

import com.google.common.collect.Lists;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

public class JavaTest {

    @Test
    public void sanityCheck() throws Exception {
        JmsQueue queue = JmsQueue.apply();
        queue.publishMessage("Hello");
        queue.publishMessage("Bonjour");
        assertEquals(queue.size(), 2);
        assertEquals(queue.toJavaList(), Lists.newArrayList("Hello", "Bonjour"));
    }
}


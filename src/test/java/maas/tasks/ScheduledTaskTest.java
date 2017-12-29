package maas.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScheduledTaskTest {
	
	@Test
	public void testGetters() {
		KneadingTask kneadingTask = new KneadingTask(40, 20, 400, 100, "order-001", "product-001");
		ScheduledTask scheduledTask = new ScheduledTask(100l, 350l, kneadingTask);
		assertEquals(100l, scheduledTask.getStart());
		assertEquals(350l, scheduledTask.getEnd());
		
		Task taskFromScheduledTask = scheduledTask.getTask();
		assertTrue(taskFromScheduledTask instanceof KneadingTask);
		
		KneadingTask kneadingTaskFromScheduledTask = (KneadingTask) taskFromScheduledTask;
		assertEquals(40l, kneadingTaskFromScheduledTask.getKneadingTime());
		assertEquals(20l, kneadingTaskFromScheduledTask.getRestingTime());
		assertEquals(400l, kneadingTaskFromScheduledTask.getDueDate());
		assertEquals(100l, kneadingTaskFromScheduledTask.getReleaseDate());
		assertEquals("order-001", kneadingTaskFromScheduledTask.getOrderId());
		assertEquals("product-001", kneadingTaskFromScheduledTask.getProductId());
	}

}

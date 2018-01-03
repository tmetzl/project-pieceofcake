package maas.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import maas.objects.Date;

public class ScheduledTaskTest {

	@Test
	public void testGetters() {
		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.setOrderId("order-001");
		kneadingTask.setProductId("Bread");
		kneadingTask.setReleaseDate(new Date(1, 2, 0, 0));
		kneadingTask.setDueDate(new Date(1, 7, 30, 0));
		kneadingTask.setKneadingTime(2400);
		kneadingTask.setRestingTime(1200);

		ScheduledTask<KneadingTask> scheduledTask = new ScheduledTask<>(new Date(1, 1, 15, 0), new Date(1, 1, 55, 0),
				kneadingTask);
		assertEquals(new Date(1, 1, 15, 0), scheduledTask.getStart());
		assertEquals(new Date(1, 1, 55, 0), scheduledTask.getEnd());

		Task taskFromScheduledTask = scheduledTask.getTask();
		assertTrue(taskFromScheduledTask instanceof KneadingTask);

		KneadingTask kneadingTaskFromScheduledTask = (KneadingTask) taskFromScheduledTask;
		assertEquals(2400l, kneadingTaskFromScheduledTask.getKneadingTime());
		assertEquals(1200l, kneadingTaskFromScheduledTask.getRestingTime());
		assertEquals(new Date(1, 7, 30, 0), kneadingTaskFromScheduledTask.getDueDate());
		assertEquals(new Date(1, 2, 0, 0), kneadingTaskFromScheduledTask.getReleaseDate());
		assertEquals("order-001", kneadingTaskFromScheduledTask.getOrderId());
		assertEquals("Bread", kneadingTaskFromScheduledTask.getProductId());
	}

}

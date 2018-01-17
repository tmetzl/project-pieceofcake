package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.tasks.BakingTask;
import org.pieceofcake.tasks.KneadingTask;
import org.pieceofcake.tasks.Task;

public class JobTest {

	@Test
	public void testGetters() {
		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.setOrderId("order-001");
		kneadingTask.setProductId("Bread");
		kneadingTask.setReleaseDate(new Date(1, 2, 0, 0));
		kneadingTask.setDueDate(new Date(1, 7, 30, 0));
		kneadingTask.setKneadingTime(2400);
		kneadingTask.setRestingTime(1200);

		Job<KneadingTask> job = new Job<>(new Date(1, 1, 15, 0), new Date(1, 1, 55, 0), kneadingTask);
		assertEquals(new Date(1, 1, 15, 0), job.getStart());
		assertEquals(new Date(1, 1, 55, 0), job.getEnd());

		Task taskFromScheduledTask = job.getAssociatedTasks().get(0);
		assertTrue(taskFromScheduledTask instanceof KneadingTask);

		KneadingTask kneadingTaskFromScheduledTask = (KneadingTask) taskFromScheduledTask;
		assertEquals(2400l, kneadingTaskFromScheduledTask.getKneadingTime());
		assertEquals(1200l, kneadingTaskFromScheduledTask.getRestingTime());
		assertEquals(new Date(1, 7, 30, 0), kneadingTaskFromScheduledTask.getDueDate());
		assertEquals(new Date(1, 2, 0, 0), kneadingTaskFromScheduledTask.getReleaseDate());
		assertEquals("order-001", kneadingTaskFromScheduledTask.getOrderId());
		assertEquals("Bread", kneadingTaskFromScheduledTask.getProductId());
	}

	@Test
	public void testAddTask() {
		BakingTask bakingTask = new BakingTask();
		bakingTask.setOrderId("order-001");
		bakingTask.setProductId("Pie");
		bakingTask.setReleaseDate(new Date(1, 2, 3, 4));
		bakingTask.setDueDate(new Date(1, 12, 3, 4));
		bakingTask.setBakingTemperature(200);
		bakingTask.setBakingTime(3000);
		bakingTask.setNumOfItems(10);

		Job<BakingTask> job = new Job<>(new Date(1, 3, 0, 0), new Date(1, 3, 50, 0), bakingTask);

		BakingTask bakingTask2 = new BakingTask();
		bakingTask2.setOrderId("order-001");
		bakingTask2.setProductId("Pie");
		bakingTask2.setReleaseDate(new Date(1, 2, 3, 4));
		bakingTask2.setDueDate(new Date(1, 12, 3, 4));
		bakingTask2.setBakingTemperature(200);
		bakingTask2.setBakingTime(3000);
		bakingTask2.setNumOfItems(10);

		job.addTask(bakingTask2);

		List<BakingTask> tasks = job.getAssociatedTasks();
		assertEquals(2, tasks.size());
		assertTrue(tasks.contains(bakingTask));
		assertTrue(tasks.contains(bakingTask2));
	}

	@Test
	public void testToString() {
		Job<Task> job = new Job<Task>(new Date(1, 2, 3, 4), new Date(2, 3, 4, 5), null);
		assertEquals("(01:02:03:04, 02:03:04:05, [null])", job.toString());
	}

}

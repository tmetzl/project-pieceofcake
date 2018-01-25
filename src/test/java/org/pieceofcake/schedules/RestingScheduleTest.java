package org.pieceofcake.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.RestingTask;

public class RestingScheduleTest {
	
	private RestingSchedule schedule;
	private List<RestingTask> tasks;
	
	@Before
	public void prepareScheduleAndTasks() {
		schedule = new RestingSchedule();
		tasks = new ArrayList<>();
		RestingTask task0 = new RestingTask();
		task0.setOrderId("order-001");
		task0.setProductId("Bread");
		task0.setReleaseDate(new Date(1, 2, 0, 0));
		task0.setDueDate(new Date(1, 7, 30, 0));
		task0.setRestingTime(3000);
		task0.setNumOfItems(1);

		RestingTask task1 = new RestingTask();
		task1.setOrderId("order-001");
		task1.setProductId("Pie");
		task1.setReleaseDate(new Date(1, 1, 0, 0));
		task1.setDueDate(new Date(1, 5, 30, 0));
		task1.setRestingTime(1800);
		task1.setNumOfItems(1);

		RestingTask task2 = new RestingTask();
		task2.setOrderId("order-002");
		task2.setProductId("Donut");
		task2.setReleaseDate(new Date(1, 0, 0, 0));
		task2.setDueDate(new Date(1, 4, 0, 0));
		task2.setRestingTime(4200);
		task2.setNumOfItems(1);

		tasks.add(task0);
		tasks.add(task1);
		tasks.add(task2);
	}
	
	@Test
	public void testGetEarliestCompletionTime() {
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		Date completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1, 2, 50, 0), completionTimeTask0);
		assertEquals(new Date(1, 1, 30, 0), completionTimeTask1);
		assertEquals(new Date(1, 1, 10, 0), completionTimeTask2);
	}
	
	@Test
	public void testInsert() {
		schedule.insert(tasks.get(2));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 1, 30, 0), completionTimeTask1);
		schedule.insert(tasks.get(1));
		completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 1, 30, 0), completionTimeTask1);
		schedule.insert(tasks.get(1));
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		assertEquals(new Date(1, 2, 50, 0), completionTimeTask0);
	}
	
	@Test
	public void testGetAndRemoveTask() {
		schedule.insert(tasks.get(0));
		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(2));
		// Order of tasks is now 2-1-0

		String task0Id = tasks.get(0).getOrderId() + tasks.get(0).getProductId();
		String task1Id = tasks.get(1).getOrderId() + tasks.get(1).getProductId();
		String task2Id = tasks.get(2).getOrderId() + tasks.get(2).getProductId();
		String taskId;
		Job<RestingTask> nextScheduledJob;
		RestingTask task;

		nextScheduledJob = schedule.getNextScheduledJob();
		task = nextScheduledJob.getAssociatedTasks().get(0);
		taskId = task.getOrderId() + task.getProductId();
		assertEquals(task2Id, taskId);
		schedule.removeFirst();

		nextScheduledJob = schedule.getNextScheduledJob();
		task = nextScheduledJob.getAssociatedTasks().get(0);
		taskId = task.getOrderId() + task.getProductId();
		assertEquals(task1Id, taskId);
		schedule.removeFirst();

		nextScheduledJob = schedule.getNextScheduledJob();
		task = nextScheduledJob.getAssociatedTasks().get(0);
		taskId = task.getOrderId() + task.getProductId();
		assertEquals(task0Id, taskId);
		schedule.removeFirst();

		nextScheduledJob = schedule.getNextScheduledJob();
		assertNull(nextScheduledJob);
		schedule.removeFirst();
		
		assertNull(schedule.addToJob(null, tasks.get(0)));
	}


}

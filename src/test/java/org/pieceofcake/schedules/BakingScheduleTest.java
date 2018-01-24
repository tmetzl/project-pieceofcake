package org.pieceofcake.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.BakingTask;

public class BakingScheduleTest {
	
	private BakingSchedule schedule;
	private List<BakingTask> tasks;
	
	@Before
	public void prepareScheduleAndTasks() {
		schedule = new BakingSchedule(1, 2, 40);
		tasks = new ArrayList<>();
		BakingTask task0 = new BakingTask();
		task0.setOrderId("order-001");
		task0.setProductId("Bread");
		task0.setReleaseDate(new Date(1, 2, 0, 0));
		task0.setDueDate(new Date(1, 7, 30, 0));
		task0.setBakingTemperature(200);
		task0.setBakingTime(1200);
		task0.setItemPerTray(10);
		task0.setNumOfItems(20);

		BakingTask task1 = new BakingTask();
		task1.setOrderId("order-001");
		task1.setProductId("Pie");
		task1.setReleaseDate(new Date(1, 1, 0, 0));
		task1.setDueDate(new Date(1, 5, 30, 0));
		task1.setBakingTemperature(250);
		task1.setBakingTime(1800);
		task1.setItemPerTray(15);
		task1.setNumOfItems(20);

		BakingTask task2 = new BakingTask();
		task2.setOrderId("order-002");
		task2.setProductId("Donut");
		task2.setReleaseDate(new Date(1, 0, 0, 0));
		task2.setDueDate(new Date(1, 4, 0, 0));
		task2.setBakingTemperature(180);
		task2.setBakingTime(600);
		task2.setItemPerTray(30);
		task2.setNumOfItems(20);

		tasks.add(task0);
		tasks.add(task1);
		tasks.add(task2);
	}
	
	@Test
	public void testGetEarliestCompletionTime() {
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		Date completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1, 2, 45, 20), completionTimeTask0);
		assertEquals(new Date(1, 2, 7, 0), completionTimeTask1);
		assertEquals(new Date(1, 0, 14, 40), completionTimeTask2);
	}
	
	@Test
	public void testInsert() {
		schedule.insert(tasks.get(2));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 2, 2, 20), completionTimeTask1);
		schedule.insert(tasks.get(1));
		
		completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 2, 32, 20), completionTimeTask1);
		
		schedule.insert(tasks.get(1));
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		assertEquals(new Date(1, 3, 13, 10), completionTimeTask0);
	}
	
	@Test
	public void testGetAndRemoveTask() {
		schedule.insert(tasks.get(0));
		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(2));
		System.out.println(schedule.getSchedule());
		// Order of tasks is now 2-1-0-0-1

		String task0Id = tasks.get(0).getOrderId() + tasks.get(0).getProductId();
		String task1Id = tasks.get(1).getOrderId() + tasks.get(1).getProductId();
		String task2Id = tasks.get(2).getOrderId() + tasks.get(2).getProductId();
		String taskId;
		Job<BakingTask> nextScheduledJob;
		BakingTask task;

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
		task = nextScheduledJob.getAssociatedTasks().get(0);
		taskId = task.getOrderId() + task.getProductId();
		assertEquals(task0Id, taskId);
		schedule.removeFirst();
		
		nextScheduledJob = schedule.getNextScheduledJob();
		task = nextScheduledJob.getAssociatedTasks().get(0);
		taskId = task.getOrderId() + task.getProductId();
		assertEquals(task1Id, taskId);
		schedule.removeFirst();

		nextScheduledJob = schedule.getNextScheduledJob();
		assertNull(nextScheduledJob);
		schedule.removeFirst();
	}


}

package org.pieceofcake.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.schedules.ItemPrepSchedule;
import org.pieceofcake.tasks.ItemPrepTask;

public class ItemPrepScheduleTest {

	private ItemPrepSchedule schedule;
	private List<ItemPrepTask> tasks;

	@Before
	public void prepareScheduleAndTasks() {
		schedule = new ItemPrepSchedule();
		tasks = new ArrayList<>();

		ItemPrepTask task0 = new ItemPrepTask();
		task0.setOrderId("order-001");
		task0.setProductId("Bread");
		task0.setReleaseDate(new Date(1, 2, 0, 0));
		task0.setDueDate(new Date(1, 7, 30, 0));
		task0.setItemPrepTime(600);
		task0.setNumOfItems(5);

		ItemPrepTask task1 = new ItemPrepTask();
		task1.setOrderId("order-001");
		task1.setProductId("Cake");
		task1.setReleaseDate(new Date(1, 1, 0, 0));
		task1.setDueDate(new Date(1, 5, 0, 0));
		task1.setItemPrepTime(360);
		task1.setNumOfItems(7);

		ItemPrepTask task2 = new ItemPrepTask();
		task2.setOrderId("order-002");
		task2.setProductId("Pie");
		task2.setReleaseDate(new Date(1, 0, 0, 0));
		task2.setDueDate(new Date(1, 4, 0, 0));
		task2.setItemPrepTime(720);
		task2.setNumOfItems(8);

		tasks.add(task0);
		tasks.add(task1);
		tasks.add(task2);
	}

	@Test
	public void testGetEarliestCompletionTime() {
		// Test with empty schedule first
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		Date completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1, 2, 50, 0), completionTimeTask0);
		assertEquals(new Date(1, 1, 42, 0), completionTimeTask1);
		assertEquals(new Date(1, 1, 36, 0), completionTimeTask2);
		
		// Test after inserting task 0
		schedule.insert(tasks.get(0));
		completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1, 1, 42, 0), completionTimeTask1);
		assertEquals(new Date(1, 1, 36, 0), completionTimeTask2);	
		
		// Test after inserting task 1
		schedule.insert(tasks.get(1));
		completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1, 3, 14, 0), completionTimeTask2);	
	}

	@Test
	public void testInsert() {
		schedule.insert(tasks.get(2));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 2, 18, 0), completionTimeTask1);
		schedule.insert(tasks.get(1));
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		assertEquals(new Date(1, 3, 8, 0), completionTimeTask0);
		schedule.insert(tasks.get(0));
	}

	@Test
	public void testGetAndRemoveTask() {
		schedule.insert(tasks.get(0));
		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(2));
		Job<ItemPrepTask> nextScheduledTask;

		nextScheduledTask = schedule.getNextScheduledJob();
		assertEquals(new Date(1, 0, 0, 0), nextScheduledTask.getStart());
		assertEquals(new Date(1, 1, 0, 0), nextScheduledTask.getEnd());
		assertEquals("order-002", nextScheduledTask.getTask().getOrderId());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());

		schedule.removeFirst();

		nextScheduledTask = schedule.getNextScheduledJob();
		assertEquals(new Date(1, 1, 0, 0), nextScheduledTask.getStart());
		assertEquals(new Date(1, 1, 42, 0), nextScheduledTask.getEnd());
		assertEquals("order-001", nextScheduledTask.getTask().getOrderId());
		assertEquals("Cake", nextScheduledTask.getTask().getProductId());

		schedule.removeFirst();

		nextScheduledTask = schedule.getNextScheduledJob();
		assertEquals(new Date(1, 1, 42, 0), nextScheduledTask.getStart());
		assertEquals(new Date(1, 1, 54, 0), nextScheduledTask.getEnd());
		assertEquals("order-002", nextScheduledTask.getTask().getOrderId());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());

		schedule.removeFirst();

		nextScheduledTask = schedule.getNextScheduledJob();
		assertEquals(new Date(1, 2, 00, 0), nextScheduledTask.getStart());
		assertEquals(new Date(1, 2, 50, 0), nextScheduledTask.getEnd());
		assertEquals("order-001", nextScheduledTask.getTask().getOrderId());
		assertEquals("Bread", nextScheduledTask.getTask().getProductId());

		schedule.removeFirst();

		nextScheduledTask = schedule.getNextScheduledJob();
		assertEquals(new Date(1, 2, 50, 0), nextScheduledTask.getStart());
		assertEquals(new Date(1, 3, 14, 0), nextScheduledTask.getEnd());
		assertEquals("order-002", nextScheduledTask.getTask().getOrderId());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());

		schedule.removeFirst();

		nextScheduledTask = schedule.getNextScheduledJob();
		assertNull(nextScheduledTask);
		
		schedule.removeFirst();
	}

}

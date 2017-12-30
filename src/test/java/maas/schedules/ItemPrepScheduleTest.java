package maas.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import maas.tasks.ItemPrepTask;
import maas.tasks.ScheduledTask;

public class ItemPrepScheduleTest {
	
	private ItemPrepSchedule schedule;
	private List<ItemPrepTask> tasks;
	
	@Before
	public void prepareScheduleAndTasks() {
		schedule = new ItemPrepSchedule();
		tasks = new ArrayList<>();
		ItemPrepTask task0 = new ItemPrepTask(1, 10, 5, 600, 40, "order-001", "Bread");
		ItemPrepTask task1 = new ItemPrepTask(1, 6, 4, 200, 100, "order-001", "Cake");
		ItemPrepTask task2 = new ItemPrepTask(1, 12, 8, 300, 0, "order-002", "Pie");
		tasks.add(task0);
		tasks.add(task1);
		tasks.add(task2);
	}
	
	@Test
	public void testGetEarliestCompletionTime() {
		long completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		long completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		long completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(90, completionTimeTask0);
		assertEquals(124, completionTimeTask1);
		assertEquals(96, completionTimeTask2);
	}
	
	@Test
	public void testInsert() {
		schedule.insert(tasks.get(0));
		long completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(146, completionTimeTask2);
		
		schedule.insert(tasks.get(2));
		long completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(170, completionTimeTask1);
	}
	
	@Test
	public void testGetAndRemoveTask() {
		schedule.insert(tasks.get(0));
		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(2));
		
		ScheduledTask<ItemPrepTask> nextScheduledTask;
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertEquals(0, nextScheduledTask.getStart());
		assertEquals(40, nextScheduledTask.getEnd());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());
		assertEquals(5, nextScheduledTask.getTask().getNumOfItems());
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertEquals(40, nextScheduledTask.getStart());
		assertEquals(90, nextScheduledTask.getEnd());
		assertEquals("Bread", nextScheduledTask.getTask().getProductId());
		assertEquals(10, nextScheduledTask.getTask().getNumOfItems());
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertEquals(90, nextScheduledTask.getStart());
		assertEquals(98, nextScheduledTask.getEnd());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());
		assertEquals(1, nextScheduledTask.getTask().getNumOfItems());
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertEquals(100, nextScheduledTask.getStart());
		assertEquals(124, nextScheduledTask.getEnd());
		assertEquals("Cake", nextScheduledTask.getTask().getProductId());
		assertEquals(6, nextScheduledTask.getTask().getNumOfItems());
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertEquals(124, nextScheduledTask.getStart());
		assertEquals(172, nextScheduledTask.getEnd());
		assertEquals("Pie", nextScheduledTask.getTask().getProductId());
		assertEquals(6, nextScheduledTask.getTask().getNumOfItems());
		schedule.removeFirst();
		
		assertNull(schedule.getNextScheduledTask());
	}
	

}

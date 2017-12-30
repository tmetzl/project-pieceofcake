package maas.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import maas.tasks.KneadingTask;
import maas.tasks.ScheduledTask;

public class KneadingScheduleTest {

	private KneadingSchedule schedule;
	private List<KneadingTask> tasks;

	@Before
	public void prepareScheduleAndTasks() {
		schedule = new KneadingSchedule();
		tasks = new ArrayList<>();
		KneadingTask task1 = new KneadingTask(1, 50, 20, 300, 200, "order-001", "Bread");
		KneadingTask task2 = new KneadingTask(1, 30, 40, 400, 100, "order-001", "Pie");
		KneadingTask task3 = new KneadingTask(1, 70, 15, 500, 220, "order-002", "Donut");
		tasks.add(task1);
		tasks.add(task2);
		tasks.add(task3);
	}

	@Test
	public void testGetEarliestCompletionTime() {
		long completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(0));
		long completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(1));
		long completionTimeTask3 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(270, completionTimeTask1);
		assertEquals(170, completionTimeTask2);
		assertEquals(305, completionTimeTask3);
	}

	@Test
	public void testInsert() {
		schedule.insert(tasks.get(0));
		long completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(170, completionTimeTask2);

		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(1));
		long completionTimeTask3 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(335, completionTimeTask3);

		schedule.insert(tasks.get(2));
		long completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(0));
		assertEquals(270, completionTimeTask1);
	}

	@Test
	public void testGetAndRemoveTask() {
		schedule.insert(tasks.get(0));
		schedule.insert(tasks.get(1));
		schedule.insert(tasks.get(2));
		// Order of tasks is now 1-0-2
		
		String task0Id = tasks.get(0).getOrderId() + tasks.get(0).getProductId();
		String task1Id = tasks.get(1).getOrderId() + tasks.get(1).getProductId();
		String task2Id = tasks.get(2).getOrderId() + tasks.get(2).getProductId();
		String taskId;
		ScheduledTask<KneadingTask> nextScheduledTask;

		nextScheduledTask = schedule.getNextScheduledTask();
		taskId = nextScheduledTask.getTask().getOrderId() + nextScheduledTask.getTask().getProductId();		
		assertEquals(task1Id, taskId);
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		taskId = nextScheduledTask.getTask().getOrderId() + nextScheduledTask.getTask().getProductId();		
		assertEquals(task0Id, taskId);
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		taskId = nextScheduledTask.getTask().getOrderId() + nextScheduledTask.getTask().getProductId();		
		assertEquals(task2Id, taskId);
		schedule.removeFirst();
		
		nextScheduledTask = schedule.getNextScheduledTask();
		assertNull(nextScheduledTask);
		schedule.removeFirst();
	}

}

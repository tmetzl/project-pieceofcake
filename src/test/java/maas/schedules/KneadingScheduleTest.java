package maas.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import maas.objects.Date;
import maas.tasks.KneadingTask;
import maas.tasks.ScheduledTask;

public class KneadingScheduleTest {

	private KneadingSchedule schedule;
	private List<KneadingTask> tasks;

	@Before
	public void prepareScheduleAndTasks() {
		schedule = new KneadingSchedule();
		tasks = new ArrayList<>();
		KneadingTask task0 = new KneadingTask();
		task0.setOrderId("order-001");
		task0.setProductId("Bread");
		task0.setReleaseDate(new Date(1, 2, 0, 0));
		task0.setDueDate(new Date(1, 7, 30, 0));
		task0.setKneadingTime(3000);
		task0.setRestingTime(1200);
		
		KneadingTask task1 = new KneadingTask();
		task1.setOrderId("order-001");
		task1.setProductId("Pie");
		task1.setReleaseDate(new Date(1, 1, 0, 0));
		task1.setDueDate(new Date(1, 5, 30, 0));
		task1.setKneadingTime(1800);
		task1.setRestingTime(2400);
		
		KneadingTask task2 = new KneadingTask();
		task2.setOrderId("order-002");
		task2.setProductId("Donut");
		task2.setReleaseDate(new Date(1, 0, 0, 0));
		task2.setDueDate(new Date(1, 4, 0, 0));
		task2.setKneadingTime(4200);
		task2.setRestingTime(900);

		tasks.add(task0);
		tasks.add(task1);
		tasks.add(task2);
	}

	@Test
	public void testGetEarliestCompletionTime() {
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		Date completionTimeTask2 = schedule.getEarliestCompletionTime(tasks.get(2));
		assertEquals(new Date(1,3,10,0), completionTimeTask0);
		assertEquals(new Date(1,2,10,0), completionTimeTask1);
		assertEquals(new Date(1,1,25,0), completionTimeTask2);
	}

	@Test
	public void testInsert() {
		schedule.insert(tasks.get(2));
		Date completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 2, 20, 0), completionTimeTask1);
		schedule.insert(tasks.get(1));
		completionTimeTask1 = schedule.getEarliestCompletionTime(tasks.get(1));
		assertEquals(new Date(1, 2, 20, 0), completionTimeTask1);
		schedule.insert(tasks.get(1));
		Date completionTimeTask0 = schedule.getEarliestCompletionTime(tasks.get(0));
		assertEquals(new Date(1, 3, 10, 0), completionTimeTask0);
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

package org.pieceofcake.machines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Date;
import org.pieceofcake.tasks.KneadingTask;

import jade.core.behaviours.Behaviour;

public class KneadingMachineTest {
	
	private KneadingTask task;
	
	@Before
	public void prepareTask() {
		task = new KneadingTask();
		task.setOrderId("order-001");
		task.setProductId("Bread");
		task.setReleaseDate(new Date(4, 0, 0, 0));
		task.setDueDate(new Date(4, 7, 30, 0));
		task.setKneadingTime(1000);
		task.setNumOfItems(1);
	}
	
	@Test
	public void testGetters() {
		KneadingMachine machine = new KneadingMachine("Test-Bakery");
		
		assertEquals(Services.KNEAD, machine.getServiceType());
		assertEquals(Protocols.KNEAD, machine.getProtocol());
		assertEquals("Test-Bakery", machine.getBakeryName());
	}
	
	@Test
	public void testGetTask() {
		KneadingMachine machine = new KneadingMachine("Test-Bakery");
		
		String msg = task.toJSONObject().toString();
		
		KneadingTask taskFromMessage = machine.getTask(msg);
		
		assertEquals(task.getOrderId(), taskFromMessage.getOrderId());
		assertEquals(task.getProductId(), taskFromMessage.getProductId());
		assertEquals(task.getReleaseDate(), taskFromMessage.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromMessage.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromMessage.getNumOfItems());
	}
	
	@Test
	public void testGetScheduleOfDay() {
		KneadingMachine machine = new KneadingMachine("Test-Bakery");
		
		Schedule<KneadingTask> schedule = machine.getScheduleOfDay(4);
		
		assertNull(schedule.getNextScheduledJob());
		
		schedule.insert(task);
		
		KneadingTask taskFromSchedule = machine.getScheduleOfDay(4).getNextScheduledJob().getAssociatedTasks().get(0);
		
		assertEquals(task.getOrderId(), taskFromSchedule.getOrderId());
		assertEquals(task.getProductId(), taskFromSchedule.getProductId());
		assertEquals(task.getReleaseDate(), taskFromSchedule.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromSchedule.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromSchedule.getNumOfItems());
		
	}
	
	@Test
	public void testGetJobProcessor() {
		KneadingMachine machine = new KneadingMachine("Test-Bakery");
		
		Schedule<KneadingTask> schedule = machine.getScheduleOfDay(4);	
		schedule.insert(task);
		
		Behaviour behaviour = machine.getJobProcessor(schedule.getNextScheduledJob());
		assertNotNull(behaviour);
	}

}

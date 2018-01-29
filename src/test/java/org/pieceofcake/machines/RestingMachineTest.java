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
import org.pieceofcake.tasks.RestingTask;

import jade.core.behaviours.Behaviour;

public class RestingMachineTest {
	
	private RestingTask task;
	
	@Before
	public void prepareTask() {
		task = new RestingTask();
		task.setOrderId("order-001");
		task.setProductId("Bread");
		task.setReleaseDate(new Date(4, 0, 0, 0));
		task.setDueDate(new Date(4, 7, 30, 0));
		task.setRestingTime(1000);
		task.setNumOfItems(1);
	}
	
	@Test
	public void testGetters() {
		RestingMachine machine = new RestingMachine("Test-Bakery");
		
		assertEquals(Services.REST, machine.getServiceType());
		assertEquals(Protocols.REST, machine.getProtocol());
		assertEquals("Test-Bakery", machine.getBakeryName());
	}
	
	@Test
	public void testGetTask() {
		RestingMachine machine = new RestingMachine("Test-Bakery");
		
		String msg = task.toJSONObject().toString();
		
		RestingTask taskFromMessage = machine.getTask(msg);
		
		assertEquals(task.getOrderId(), taskFromMessage.getOrderId());
		assertEquals(task.getProductId(), taskFromMessage.getProductId());
		assertEquals(task.getReleaseDate(), taskFromMessage.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromMessage.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromMessage.getNumOfItems());
	}
	
	@Test
	public void testGetScheduleOfDay() {
		RestingMachine machine = new RestingMachine("Test-Bakery");
		
		Schedule<RestingTask> schedule = machine.getScheduleOfDay(4);
		
		assertNull(schedule.getNextScheduledJob());
		
		schedule.insert(task);
		
		RestingTask taskFromSchedule = machine.getScheduleOfDay(4).getNextScheduledJob().getAssociatedTasks().get(0);
		
		assertEquals(task.getOrderId(), taskFromSchedule.getOrderId());
		assertEquals(task.getProductId(), taskFromSchedule.getProductId());
		assertEquals(task.getReleaseDate(), taskFromSchedule.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromSchedule.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromSchedule.getNumOfItems());
		
	}
	
	@Test
	public void testGetJobProcessor() {
		RestingMachine machine = new RestingMachine("Test-Bakery");
		
		Schedule<RestingTask> schedule = machine.getScheduleOfDay(4);	
		schedule.insert(task);
		
		Behaviour behaviour = machine.getJobProcessor(schedule.getNextScheduledJob());
		assertNotNull(behaviour);
	}

}

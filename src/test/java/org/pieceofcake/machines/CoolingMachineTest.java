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
import org.pieceofcake.tasks.CoolingTask;

import jade.core.behaviours.Behaviour;

public class CoolingMachineTest {
	
	private CoolingTask task;
	
	@Before
	public void prepareTask() {
		task = new CoolingTask();
		task.setOrderId("order-001");
		task.setProductId("Bread");
		task.setReleaseDate(new Date(4, 0, 0, 0));
		task.setDueDate(new Date(4, 7, 30, 0));
		task.setCoolingTimeFactor(2);
		task.setBakingTemperature(200);
		task.setNumOfItems(1);
	}
	
	@Test
	public void testGetters() {
		CoolingMachine machine = new CoolingMachine("Test-Bakery");
		
		assertEquals(Services.COOL, machine.getServiceType());
		assertEquals(Protocols.COOL, machine.getProtocol());
		assertEquals("Test-Bakery", machine.getBakeryName());
	}
	
	@Test
	public void testGetTask() {
		CoolingMachine machine = new CoolingMachine("Test-Bakery");
		
		String msg = task.toJSONObject().toString();
		
		CoolingTask taskFromMessage = machine.getTask(msg);
		
		assertEquals(task.getOrderId(), taskFromMessage.getOrderId());
		assertEquals(task.getProductId(), taskFromMessage.getProductId());
		assertEquals(task.getReleaseDate(), taskFromMessage.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromMessage.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromMessage.getNumOfItems());
	}
	
	@Test
	public void testGetScheduleOfDay() {
		CoolingMachine machine = new CoolingMachine("Test-Bakery");
		
		Schedule<CoolingTask> schedule = machine.getScheduleOfDay(4);
		
		assertNull(schedule.getNextScheduledJob());
		
		schedule.insert(task);
		
		CoolingTask taskFromSchedule = machine.getScheduleOfDay(4).getNextScheduledJob().getAssociatedTasks().get(0);
		
		assertEquals(task.getOrderId(), taskFromSchedule.getOrderId());
		assertEquals(task.getProductId(), taskFromSchedule.getProductId());
		assertEquals(task.getReleaseDate(), taskFromSchedule.getReleaseDate());
		assertEquals(task.getDueDate(), taskFromSchedule.getDueDate());
		assertEquals(task.getNumOfItems(), taskFromSchedule.getNumOfItems());
		
	}
	
	@Test
	public void testGetJobProcessor() {
		CoolingMachine machine = new CoolingMachine("Test-Bakery");
		
		Schedule<CoolingTask> schedule = machine.getScheduleOfDay(4);	
		schedule.insert(task);
		
		Behaviour behaviour = machine.getJobProcessor(schedule.getNextScheduledJob());
		assertNotNull(behaviour);
	}

}

package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class CoolingTaskTest {

	private CoolingTask coolingTask;
	private CoolingTask anotherCoolingTask;
	private CoolingTask oneMoreCoolingTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);

	@Before
	public void prepareRestingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("baking_temp", 100);
		jsonObject.put("cooling_time_factor", 1.2);
		jsonObject.put("item_per_box", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		coolingTask = new CoolingTask();
		coolingTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromBoxingTask = coolingTask.toJSONObject();
		anotherCoolingTask = new CoolingTask();
		anotherCoolingTask.fromJSONObject(jsonObjectFromBoxingTask);

		oneMoreCoolingTask = new CoolingTask();
		oneMoreCoolingTask = coolingTask.copy();
		oneMoreCoolingTask.setCoolingTimeFactor(1.5);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", coolingTask.getProductId());
		assertEquals(1.2, coolingTask.getCoolingTimeFactor(), 1e-10);
		assertEquals(100, coolingTask.getBakingTemperature());
		assertEquals(releaseDate, coolingTask.getReleaseDate());
		assertEquals(dueDate, coolingTask.getDueDate());
		assertEquals("order-001", coolingTask.getOrderId());

		assertEquals("Bread", anotherCoolingTask.getProductId());
		assertEquals(1.2, anotherCoolingTask.getCoolingTimeFactor(), 1e-10);
		assertEquals(100, anotherCoolingTask.getBakingTemperature());
		assertEquals(releaseDate, anotherCoolingTask.getReleaseDate());
		assertEquals(dueDate, anotherCoolingTask.getDueDate());
		assertEquals("order-001", anotherCoolingTask.getOrderId());

	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(coolingTask, anotherCoolingTask);
		assertNotEquals(coolingTask, oneMoreCoolingTask);
		assertNotEquals(coolingTask, new Object());

		assertEquals(coolingTask.hashCode(), anotherCoolingTask.hashCode());

	}

}

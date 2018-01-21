package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class CoolingTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("baking_temp", 100);
		jsonObject.put("cooling_time_factor", 1.2);
		jsonObject.put("item_per_box", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		CoolingTask coolingTask = new CoolingTask();
		coolingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", coolingTask.getProductId());
		assertEquals(1.2, coolingTask.getCoolingTimeFactor(), 1e-10);
		assertEquals(100, coolingTask.getBakingTemperature());
		assertEquals(releaseDate, coolingTask.getReleaseDate());
		assertEquals(dueDate, coolingTask.getDueDate());
		assertEquals("order-001", coolingTask.getOrderId());

		JSONObject jsonObjectFromBoxingTask = coolingTask.toJSONObject();
		CoolingTask anotherCoolingTask = new CoolingTask();
		anotherCoolingTask.fromJSONObject(jsonObjectFromBoxingTask);

		assertEquals("Bread", anotherCoolingTask.getProductId());
		assertEquals(1.2, anotherCoolingTask.getCoolingTimeFactor(), 1e-10);
		assertEquals(100, anotherCoolingTask.getBakingTemperature());
		assertEquals(releaseDate, anotherCoolingTask.getReleaseDate());
		assertEquals(dueDate, anotherCoolingTask.getDueDate());
		assertEquals("order-001", anotherCoolingTask.getOrderId());
	}

}

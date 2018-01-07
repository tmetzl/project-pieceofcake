package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.tasks.BakingTask;

public class BakingTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("baking_time", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("baking_temp", 100);
		jsonObject.put("num_of_items", 5);
		jsonObject.put("cooling_time_factor", 1.2);

		BakingTask bakingTask = new BakingTask();
		bakingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", bakingTask.getProductId());
		assertEquals(10, bakingTask.getBakingTime());
		assertEquals(releaseDate, bakingTask.getReleaseDate());
		assertEquals(dueDate, bakingTask.getDueDate());
		assertEquals("order-001", bakingTask.getOrderId());
		assertEquals(100, bakingTask.getBakingTemperature());
		assertEquals(5, bakingTask.getNumOfItems());
		assertEquals(1.2, bakingTask.getCoolingTimeFactor(), 0.01);

		JSONObject jsonObjectFromBakingTask = bakingTask.toJSONObject();
		BakingTask anotherBakingTask = new BakingTask();
		anotherBakingTask.fromJSONObject(jsonObjectFromBakingTask);

		assertEquals("Bread", anotherBakingTask.getProductId());
		assertEquals(10, anotherBakingTask.getBakingTime());
		assertEquals(releaseDate, anotherBakingTask.getReleaseDate());
		assertEquals(dueDate, anotherBakingTask.getDueDate());
		assertEquals("order-001", anotherBakingTask.getOrderId());
		assertEquals(100, anotherBakingTask.getBakingTemperature());
		assertEquals(5, anotherBakingTask.getNumOfItems());
		assertEquals(1.2, anotherBakingTask.getCoolingTimeFactor(), 0.01);
	}

}

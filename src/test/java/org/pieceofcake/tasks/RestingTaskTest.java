package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class RestingTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("resting_time", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		RestingTask restingTask = new RestingTask();
		restingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", restingTask.getProductId());
		assertEquals(10, restingTask.getRestingTime());
		assertEquals(releaseDate, restingTask.getReleaseDate());
		assertEquals(dueDate, restingTask.getDueDate());
		assertEquals("order-001", restingTask.getOrderId());

		JSONObject jsonObjectFromRestingTask = restingTask.toJSONObject();
		RestingTask anotherRestingTask = new RestingTask();
		anotherRestingTask.fromJSONObject(jsonObjectFromRestingTask);

		assertEquals("Bread", anotherRestingTask.getProductId());
		assertEquals(10, anotherRestingTask.getRestingTime());
		assertEquals(releaseDate, anotherRestingTask.getReleaseDate());
		assertEquals(dueDate, anotherRestingTask.getDueDate());
		assertEquals("order-001", anotherRestingTask.getOrderId());
	}

}

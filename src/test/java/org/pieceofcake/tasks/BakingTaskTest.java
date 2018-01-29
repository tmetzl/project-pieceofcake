package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class BakingTaskTest {

	private BakingTask bakingTask;
	private BakingTask anotherBakingTask;
	private BakingTask oneMoreBakingTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);

	@Before
	public void prepareRestingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("baking_time", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("baking_temp", 100);
		jsonObject.put("num_of_items", 5);
		jsonObject.put("item_per_tray", 10);

		bakingTask = new BakingTask();
		bakingTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromBakingTask = bakingTask.toJSONObject();
		anotherBakingTask = new BakingTask();
		anotherBakingTask.fromJSONObject(jsonObjectFromBakingTask);

		oneMoreBakingTask = new BakingTask();
		oneMoreBakingTask = bakingTask.copy();
		oneMoreBakingTask.setBakingTime(12);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", bakingTask.getProductId());
		assertEquals(10, bakingTask.getBakingTime());
		assertEquals(releaseDate, bakingTask.getReleaseDate());
		assertEquals(dueDate, bakingTask.getDueDate());
		assertEquals("order-001", bakingTask.getOrderId());
		assertEquals(100, bakingTask.getBakingTemperature());
		assertEquals(5, bakingTask.getNumOfItems());
		assertEquals(10, bakingTask.getItemPerTray());

		assertEquals("Bread", anotherBakingTask.getProductId());
		assertEquals(10, anotherBakingTask.getBakingTime());
		assertEquals(releaseDate, anotherBakingTask.getReleaseDate());
		assertEquals(dueDate, anotherBakingTask.getDueDate());
		assertEquals("order-001", anotherBakingTask.getOrderId());
		assertEquals(100, anotherBakingTask.getBakingTemperature());
		assertEquals(5, anotherBakingTask.getNumOfItems());
		assertEquals(10, anotherBakingTask.getItemPerTray());
		
	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(bakingTask, anotherBakingTask);
		assertNotEquals(bakingTask, oneMoreBakingTask);
		assertNotEquals(bakingTask, new Object());

		assertEquals(bakingTask.hashCode(), anotherBakingTask.hashCode());

	}

}

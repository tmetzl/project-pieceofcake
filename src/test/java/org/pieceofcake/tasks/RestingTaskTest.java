package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class RestingTaskTest {

	private RestingTask restingTask;
	private RestingTask anotherRestingTask;
	private RestingTask oneMoreRestingTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);

	@Before
	public void prepareRestingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("resting_time", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		restingTask = new RestingTask();
		restingTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromRestingTask = restingTask.toJSONObject();
		anotherRestingTask = new RestingTask();
		anotherRestingTask.fromJSONObject(jsonObjectFromRestingTask);

		oneMoreRestingTask = new RestingTask();
		oneMoreRestingTask = restingTask.copy();
		oneMoreRestingTask.setRestingTime(5);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", restingTask.getProductId());
		assertEquals(10, restingTask.getRestingTime());
		assertEquals(releaseDate, restingTask.getReleaseDate());
		assertEquals(dueDate, restingTask.getDueDate());
		assertEquals("order-001", restingTask.getOrderId());

		assertEquals("Bread", anotherRestingTask.getProductId());
		assertEquals(10, anotherRestingTask.getRestingTime());
		assertEquals(releaseDate, anotherRestingTask.getReleaseDate());
		assertEquals(dueDate, anotherRestingTask.getDueDate());
		assertEquals("order-001", anotherRestingTask.getOrderId());

	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(restingTask, anotherRestingTask);
		assertNotEquals(restingTask, oneMoreRestingTask);
		assertNotEquals(restingTask, new Object());

		assertEquals(restingTask.hashCode(), anotherRestingTask.hashCode());

	}

}

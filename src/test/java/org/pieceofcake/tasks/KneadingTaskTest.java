package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class KneadingTaskTest {

	private KneadingTask kneadingTask;
	private KneadingTask anotherKneadingTask;
	private KneadingTask oneMoreKneadingTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);

	@Before
	public void prepareKneadingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("kneading_time", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		kneadingTask = new KneadingTask();
		kneadingTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromKneadingTask = kneadingTask.toJSONObject();
		anotherKneadingTask = new KneadingTask();
		anotherKneadingTask.fromJSONObject(jsonObjectFromKneadingTask);

		oneMoreKneadingTask = new KneadingTask();
		oneMoreKneadingTask = kneadingTask.copy();
		oneMoreKneadingTask.setKneadingTime(9);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", kneadingTask.getProductId());
		assertEquals(10, kneadingTask.getKneadingTime());
		assertEquals(releaseDate, kneadingTask.getReleaseDate());
		assertEquals(dueDate, kneadingTask.getDueDate());
		assertEquals("order-001", kneadingTask.getOrderId());

		assertEquals("Bread", anotherKneadingTask.getProductId());
		assertEquals(10, anotherKneadingTask.getKneadingTime());
		assertEquals(releaseDate, anotherKneadingTask.getReleaseDate());
		assertEquals(dueDate, anotherKneadingTask.getDueDate());
		assertEquals("order-001", anotherKneadingTask.getOrderId());

	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(kneadingTask, anotherKneadingTask);
		assertNotEquals(kneadingTask, oneMoreKneadingTask);
		assertNotEquals(kneadingTask, new Object());

		assertEquals(kneadingTask.hashCode(), anotherKneadingTask.hashCode());

	}

}

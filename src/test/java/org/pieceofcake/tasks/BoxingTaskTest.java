package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class BoxingTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("num_of_items", 1);
		jsonObject.put("item_per_box", 10);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		BoxingTask boxingTask = new BoxingTask();
		boxingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", boxingTask.getProductId());
		assertEquals(10, boxingTask.getItemPerBox());
		assertEquals(releaseDate, boxingTask.getReleaseDate());
		assertEquals(dueDate, boxingTask.getDueDate());
		assertEquals("order-001", boxingTask.getOrderId());

		JSONObject jsonObjectFromBoxingTask = boxingTask.toJSONObject();
		BoxingTask anotherBoxingTask = new BoxingTask();
		anotherBoxingTask.fromJSONObject(jsonObjectFromBoxingTask);

		assertEquals("Bread", anotherBoxingTask.getProductId());
		assertEquals(10, anotherBoxingTask.getItemPerBox());
		assertEquals(releaseDate, anotherBoxingTask.getReleaseDate());
		assertEquals(dueDate, anotherBoxingTask.getDueDate());
		assertEquals("order-001", anotherBoxingTask.getOrderId());
	}

}

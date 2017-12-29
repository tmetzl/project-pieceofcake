package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class KneadingTaskTest {

	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("kneading_time", 10);
		jsonObject.put("resting_time", 2);
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);
		jsonObject.put("order_id", "order-001");
		jsonObject.put("day", 1);

		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", kneadingTask.getProductId());
		assertEquals(10, kneadingTask.getKneadingTime());
		assertEquals(2, kneadingTask.getRestingTime());
		assertEquals(2, kneadingTask.getReleaseDate());
		assertEquals(14, kneadingTask.getDueDate());
		assertEquals("order-001", kneadingTask.getOrderId());
		assertEquals(1, kneadingTask.getDay());

		JSONObject jsonObjectFromKneadingTask = kneadingTask.toJSONObject();

		assertEquals(7, jsonObjectFromKneadingTask.length());
		assertEquals("Bread", jsonObjectFromKneadingTask.getString("product_id"));
		assertEquals(10l, jsonObjectFromKneadingTask.getLong("kneading_time"));
		assertEquals(2l, jsonObjectFromKneadingTask.getLong("resting_time"));
		assertEquals(2l, jsonObjectFromKneadingTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromKneadingTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromKneadingTask.getString("order_id"));
		assertEquals(1, jsonObjectFromKneadingTask.getInt("day"));
		
		KneadingTask anotherKneadingTask = new KneadingTask(1, 10, 2, 14, 2, "order-001", "Bread");

		assertEquals("Bread", anotherKneadingTask.getProductId());
		assertEquals(10, anotherKneadingTask.getKneadingTime());
		assertEquals(2, anotherKneadingTask.getRestingTime());
		assertEquals(2, anotherKneadingTask.getReleaseDate());
		assertEquals(14, anotherKneadingTask.getDueDate());
		assertEquals("order-001", anotherKneadingTask.getOrderId());
		assertEquals(1, anotherKneadingTask.getDay());
	}

}

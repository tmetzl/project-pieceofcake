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

		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", kneadingTask.getProductId());
		assertEquals(10, kneadingTask.getKneadingTime());
		assertEquals(2, kneadingTask.getRestingTime());
		assertEquals(2, kneadingTask.getReleaseDate());
		assertEquals(14, kneadingTask.getDueDate());
		assertEquals("order-001", kneadingTask.getOrderId());

		JSONObject jsonObjectFromKneadingTask = kneadingTask.toJSONObject();

		assertEquals(6, jsonObjectFromKneadingTask.length());
		assertEquals("Bread", jsonObjectFromKneadingTask.getString("product_id"));
		assertEquals(10l, jsonObjectFromKneadingTask.getLong("kneading_time"));
		assertEquals(2l, jsonObjectFromKneadingTask.getLong("resting_time"));
		assertEquals(2l, jsonObjectFromKneadingTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromKneadingTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromKneadingTask.getString("order_id"));
	}

}

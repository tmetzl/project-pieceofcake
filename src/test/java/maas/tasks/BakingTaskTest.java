package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class BakingTaskTest {

	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("baking_time", 10);
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);
		jsonObject.put("order_id", "order-001");
		jsonObject.put("baking_temp", 100);
		jsonObject.put("num_of_items", 5);
		jsonObject.put("cooling_time_factor", 1.2);

		BakingTask bakingTask = new BakingTask();
		bakingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", bakingTask.getProductId());
		assertEquals(10, bakingTask.getBakingTime());
		assertEquals(2, bakingTask.getReleaseDate());
		assertEquals(14, bakingTask.getDueDate());
		assertEquals("order-001", bakingTask.getOrderId());
		assertEquals(100, bakingTask.getBakingTemperature());
		assertEquals(5, bakingTask.getNumOfItems());
		assertEquals(1.2, bakingTask.getCoolingTimeFactor(), 0.01);

		JSONObject jsonObjectFromBakingTask = new JSONObject();
		jsonObjectFromBakingTask = bakingTask.toJSONObject();

		assertEquals(8, jsonObjectFromBakingTask.length());
		assertEquals("Bread", jsonObjectFromBakingTask.getString("product_id"));
		assertEquals(10l, jsonObjectFromBakingTask.getLong("baking_time"));
		assertEquals(2l, jsonObjectFromBakingTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromBakingTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromBakingTask.getString("order_id"));
		assertEquals(100l, jsonObjectFromBakingTask.getLong("baking_temp"));
		assertEquals(5, jsonObjectFromBakingTask.getInt("num_of_items"));
		assertEquals(1.2, jsonObjectFromBakingTask.getDouble("cooling_time_factor"), 0.01);
	}

}

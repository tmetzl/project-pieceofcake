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

		BakingTask bakingTask = new BakingTask();
		bakingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", bakingTask.getProductId());
		assertEquals(10, bakingTask.getBakingTime());
		assertEquals(2, bakingTask.getReleaseDate());
		assertEquals(14, bakingTask.getDueDate());
		assertEquals("order-001", bakingTask.getOrderId());
		assertEquals(100, bakingTask.getBakingTemperature());
		assertEquals(5, bakingTask.getNumOfItems());

		JSONObject jsonObjectFromBakingTask = new JSONObject();
		jsonObjectFromBakingTask = bakingTask.toJSONObject();

		assertEquals(7, jsonObjectFromBakingTask.length());
		assertEquals("Bread", jsonObjectFromBakingTask.get("product_id"));
		assertEquals(10l, jsonObjectFromBakingTask.get("baking_time"));
		assertEquals(2l, jsonObjectFromBakingTask.get("release_date"));
		assertEquals(14l, jsonObjectFromBakingTask.get("due_date"));
		assertEquals("order-001", jsonObjectFromBakingTask.get("order_id"));
		assertEquals(100l, jsonObjectFromBakingTask.get("baking_temp"));
		assertEquals(5, jsonObjectFromBakingTask.get("num_of_items"));
	}

}

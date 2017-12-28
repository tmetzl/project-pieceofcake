package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class BoxingTaskTest {
	
	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);
		jsonObject.put("order_id", "order-001");
		jsonObject.put("boxing_temp", 20);
		jsonObject.put("num_of_items", 5);

		BoxingTask boxingTask = new BoxingTask();
		boxingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", boxingTask.getProductId());
		assertEquals(2, boxingTask.getReleaseDate());
		assertEquals(14, boxingTask.getDueDate());
		assertEquals("order-001", boxingTask.getOrderId());
		assertEquals(20, boxingTask.getBoxingTemperature());
		assertEquals(5, boxingTask.getNumOfItems());

		JSONObject jsonObjectFromBoxingTask = new JSONObject();
		jsonObjectFromBoxingTask = boxingTask.toJSONObject();

		assertEquals(6, jsonObjectFromBoxingTask.length());
		assertEquals("Bread", jsonObjectFromBoxingTask.get("product_id"));
		assertEquals(2l, jsonObjectFromBoxingTask.get("release_date"));
		assertEquals(14l, jsonObjectFromBoxingTask.get("due_date"));
		assertEquals("order-001", jsonObjectFromBoxingTask.get("order_id"));
		assertEquals(20l, jsonObjectFromBoxingTask.get("boxing_temp"));
		assertEquals(5, jsonObjectFromBoxingTask.get("num_of_items"));
	}

}

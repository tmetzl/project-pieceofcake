package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class DeliveryTaskTest {
	
	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_boxes", 5);
		jsonObject.put("loc_x", 0);
		jsonObject.put("loc_y", 0);

		DeliveryTask deliveryTask = new DeliveryTask();
		deliveryTask.fromJSONObject(jsonObject);

		assertEquals("Bread", deliveryTask.getProductId());
		assertEquals(2, deliveryTask.getReleaseDate());
		assertEquals(14, deliveryTask.getDueDate());
		assertEquals("order-001", deliveryTask.getOrderId());
		assertEquals(5, deliveryTask.getNumOfBoxes());
		assertEquals(0.0, deliveryTask.getLocationX(), 0.001);
		assertEquals(0.0, deliveryTask.getLocationY(), 0.001);

		JSONObject jsonObjectFromDeliveryTask = deliveryTask.toJSONObject();

		assertEquals(7, jsonObjectFromDeliveryTask.length());
		assertEquals("Bread", jsonObjectFromDeliveryTask.getString("product_id"));
		assertEquals(2l, jsonObjectFromDeliveryTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromDeliveryTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromDeliveryTask.getString("order_id"));
		assertEquals(5, jsonObjectFromDeliveryTask.getInt("num_of_boxes"));
		assertEquals(0.0, jsonObjectFromDeliveryTask.getDouble("loc_x"), 0.01);
		assertEquals(0.0, jsonObjectFromDeliveryTask.getDouble("loc_y"), 0.01);
	}

}

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
		jsonObject.put("customer_id", "customer_001");
		jsonObject.put("day", 1);

		DeliveryTask deliveryTask = new DeliveryTask();
		deliveryTask.fromJSONObject(jsonObject);

		assertEquals("Bread", deliveryTask.getProductId());
		assertEquals(2, deliveryTask.getReleaseDate());
		assertEquals(14, deliveryTask.getDueDate());
		assertEquals("order-001", deliveryTask.getOrderId());
		assertEquals(5, deliveryTask.getNumOfBoxes());
		assertEquals("customer_001", deliveryTask.getCustomerId());
		assertEquals(1, deliveryTask.getDay());

		JSONObject jsonObjectFromDeliveryTask = deliveryTask.toJSONObject();

		assertEquals(7, jsonObjectFromDeliveryTask.length());
		assertEquals("Bread", jsonObjectFromDeliveryTask.getString("product_id"));
		assertEquals(2l, jsonObjectFromDeliveryTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromDeliveryTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromDeliveryTask.getString("order_id"));
		assertEquals(5, jsonObjectFromDeliveryTask.getInt("num_of_boxes"));
		assertEquals("customer_001", jsonObjectFromDeliveryTask.getString("customer_id"));
		assertEquals(1, jsonObjectFromDeliveryTask.getInt("day"));

		DeliveryTask anotherDeliveryTask = new DeliveryTask(2, 5, "customer_002", 5, 1, "order-002", "Cake");

		assertEquals("Cake", anotherDeliveryTask.getProductId());
		assertEquals(1, anotherDeliveryTask.getReleaseDate());
		assertEquals(5, anotherDeliveryTask.getDueDate());
		assertEquals("order-002", anotherDeliveryTask.getOrderId());
		assertEquals(5, anotherDeliveryTask.getNumOfBoxes());
		assertEquals("customer_002", anotherDeliveryTask.getCustomerId());
		assertEquals(2, anotherDeliveryTask.getDay());
	}

}

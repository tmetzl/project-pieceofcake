package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import com.sun.xml.internal.bind.marshaller.DumbEscapeHandler;

import maas.objects.Date;
import maas.objects.Location;

public class DeliveryTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		Location location = new Location(5.0, 5.0);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_items", 10);
		jsonObject.put("item_per_box", 5);
		jsonObject.put("location", location.toJSONObject());

		DeliveryTask deliveryTask = new DeliveryTask();
		deliveryTask.fromJSONObject(jsonObject);

		assertEquals("Bread", deliveryTask.getProductId());
		assertEquals(releaseDate, deliveryTask.getReleaseDate());
		assertEquals(dueDate, deliveryTask.getDueDate());
		assertEquals("order-001", deliveryTask.getOrderId());
		assertEquals(5, deliveryTask.getItemPerBox());
		assertEquals(10, deliveryTask.getNumOfItems());
		assertEquals(location, deliveryTask.getLocation());

		JSONObject jsonObjectFromDeliveryTask = deliveryTask.toJSONObject();
		DeliveryTask anotherDeliveryTask = new DeliveryTask();
		anotherDeliveryTask.fromJSONObject(jsonObjectFromDeliveryTask);

		assertEquals("Bread", anotherDeliveryTask.getProductId());
		assertEquals(releaseDate, anotherDeliveryTask.getReleaseDate());
		assertEquals(dueDate, anotherDeliveryTask.getDueDate());
		assertEquals("order-001", anotherDeliveryTask.getOrderId());
		assertEquals(location, anotherDeliveryTask.getLocation());
		assertEquals(5, anotherDeliveryTask.getItemPerBox());
		assertEquals(10, anotherDeliveryTask.getNumOfItems());
	}
}

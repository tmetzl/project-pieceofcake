package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Location;

public class DeliveryTaskTest {

	private DeliveryTask deliveryTask;
	private DeliveryTask anotherDeliveryTask;
	private DeliveryTask oneMoreDeliveryTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);
	private Location location = new Location(5.0, 5.0);

	@Before
	public void prepareRestingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_items", 10);
		jsonObject.put("item_per_box", 5);
		jsonObject.put("location", location.toJSONObject());

		deliveryTask = new DeliveryTask();
		deliveryTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromDeliveryTask = deliveryTask.toJSONObject();
		anotherDeliveryTask = new DeliveryTask();
		anotherDeliveryTask.fromJSONObject(jsonObjectFromDeliveryTask);

		oneMoreDeliveryTask = new DeliveryTask();
		oneMoreDeliveryTask = deliveryTask.copy();
		oneMoreDeliveryTask.setItemPerBox(15);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", deliveryTask.getProductId());
		assertEquals(releaseDate, deliveryTask.getReleaseDate());
		assertEquals(dueDate, deliveryTask.getDueDate());
		assertEquals("order-001", deliveryTask.getOrderId());
		assertEquals(5, deliveryTask.getItemPerBox());
		assertEquals(10, deliveryTask.getNumOfItems());
		assertEquals(location, deliveryTask.getLocation());

		assertEquals("Bread", anotherDeliveryTask.getProductId());
		assertEquals(releaseDate, anotherDeliveryTask.getReleaseDate());
		assertEquals(dueDate, anotherDeliveryTask.getDueDate());
		assertEquals("order-001", anotherDeliveryTask.getOrderId());
		assertEquals(location, anotherDeliveryTask.getLocation());
		assertEquals(5, anotherDeliveryTask.getItemPerBox());
		assertEquals(10, anotherDeliveryTask.getNumOfItems());

	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(deliveryTask, anotherDeliveryTask);
		assertNotEquals(deliveryTask, oneMoreDeliveryTask);
		assertNotEquals(deliveryTask, new Object());

		assertEquals(deliveryTask.hashCode(), anotherDeliveryTask.hashCode());

	}

}

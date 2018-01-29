package org.pieceofcake.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;

public class ItemPrepTaskTest {

	private ItemPrepTask itemPrepTask;
	private ItemPrepTask anotherItemPrepTask;
	private ItemPrepTask oneMoreItemPrepTask;
	private Date releaseDate = new Date(0, 3, 1, 0);
	private Date dueDate = new Date(1, 2, 3, 4);

	@Before
	public void prepareRestingTasks() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("item_prep_time", 2);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_items", 5);

		itemPrepTask = new ItemPrepTask();
		itemPrepTask.fromJSONObject(jsonObject);

		JSONObject jsonObjectFromItemPrepTask = itemPrepTask.toJSONObject();
		anotherItemPrepTask = new ItemPrepTask();
		anotherItemPrepTask.fromJSONObject(jsonObjectFromItemPrepTask);

		oneMoreItemPrepTask = new ItemPrepTask();
		oneMoreItemPrepTask = itemPrepTask.copy();
		oneMoreItemPrepTask.setItemPrepTime(5);

	}

	@Test
	public void testGettersAndSetters() {

		assertEquals("Bread", itemPrepTask.getProductId());
		assertEquals(2, itemPrepTask.getItemPrepTime());
		assertEquals(releaseDate, itemPrepTask.getReleaseDate());
		assertEquals(dueDate, itemPrepTask.getDueDate());
		assertEquals("order-001", itemPrepTask.getOrderId());
		assertEquals(5, itemPrepTask.getNumOfItems());

		assertEquals("Bread", anotherItemPrepTask.getProductId());
		assertEquals(2, anotherItemPrepTask.getItemPrepTime());
		assertEquals(releaseDate, anotherItemPrepTask.getReleaseDate());
		assertEquals(dueDate, anotherItemPrepTask.getDueDate());
		assertEquals("order-001", anotherItemPrepTask.getOrderId());
		assertEquals(5, anotherItemPrepTask.getNumOfItems());

	}

	@Test
	public void testEqualsAndHashCode() {

		assertEquals(itemPrepTask, anotherItemPrepTask);
		assertNotEquals(itemPrepTask, oneMoreItemPrepTask);
		assertNotEquals(itemPrepTask, new Object());

		assertEquals(itemPrepTask.hashCode(), anotherItemPrepTask.hashCode());

	}

}

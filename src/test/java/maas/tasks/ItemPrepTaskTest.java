package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import maas.objects.Date;

public class ItemPrepTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("item_prep_time", 2);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_items", 5);

		ItemPrepTask itemPrepTask = new ItemPrepTask();
		itemPrepTask.fromJSONObject(jsonObject);

		assertEquals("Bread", itemPrepTask.getProductId());
		assertEquals(2, itemPrepTask.getItemPrepTime());
		assertEquals(releaseDate, itemPrepTask.getReleaseDate());
		assertEquals(dueDate, itemPrepTask.getDueDate());
		assertEquals("order-001", itemPrepTask.getOrderId());
		assertEquals(5, itemPrepTask.getNumOfItems());

		JSONObject jsonObjectFromItemPrepTask = itemPrepTask.toJSONObject();
		ItemPrepTask anotherItemPrepTask = new ItemPrepTask();
		anotherItemPrepTask.fromJSONObject(jsonObjectFromItemPrepTask);

		assertEquals("Bread", anotherItemPrepTask.getProductId());
		assertEquals(2, anotherItemPrepTask.getItemPrepTime());
		assertEquals(releaseDate, anotherItemPrepTask.getReleaseDate());
		assertEquals(dueDate, anotherItemPrepTask.getDueDate());
		assertEquals("order-001", anotherItemPrepTask.getOrderId());
		assertEquals(5, anotherItemPrepTask.getNumOfItems());
	}

}

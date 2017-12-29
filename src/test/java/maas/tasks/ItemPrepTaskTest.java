package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class ItemPrepTaskTest {

	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("item_prep_time", 2);
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);
		jsonObject.put("order_id", "order-001");
		jsonObject.put("num_of_items", 5);
		jsonObject.put("day", 1);

		ItemPrepTask itemPrepTask = new ItemPrepTask();
		itemPrepTask.fromJSONObject(jsonObject);

		assertEquals("Bread", itemPrepTask.getProductId());
		assertEquals(2, itemPrepTask.getItemPrepTime());
		assertEquals(2, itemPrepTask.getReleaseDate());
		assertEquals(14, itemPrepTask.getDueDate());
		assertEquals("order-001", itemPrepTask.getOrderId());
		assertEquals(5, itemPrepTask.getNumOfItems());
		assertEquals(1, itemPrepTask.getDay());

		JSONObject jsonObjectFromItemPrepTask = new JSONObject();
		jsonObjectFromItemPrepTask = itemPrepTask.toJSONObject();

		assertEquals(7, jsonObjectFromItemPrepTask.length());
		assertEquals("Bread", jsonObjectFromItemPrepTask.getString("product_id"));
		assertEquals(2l, jsonObjectFromItemPrepTask.getLong("item_prep_time"));
		assertEquals(2l, jsonObjectFromItemPrepTask.getLong("release_date"));
		assertEquals(14l, jsonObjectFromItemPrepTask.getLong("due_date"));
		assertEquals("order-001", jsonObjectFromItemPrepTask.getString("order_id"));
		assertEquals(5, jsonObjectFromItemPrepTask.getInt("num_of_items"));
		assertEquals(1, jsonObjectFromItemPrepTask.getInt("day"));

		ItemPrepTask anotherItemPrepTask = new ItemPrepTask(1, 3, 2, 10, 2, "order-002", "Cake");

		assertEquals("Cake", anotherItemPrepTask.getProductId());
		assertEquals(2, anotherItemPrepTask.getItemPrepTime());
		assertEquals(2, anotherItemPrepTask.getReleaseDate());
		assertEquals(10, anotherItemPrepTask.getDueDate());
		assertEquals("order-002", anotherItemPrepTask.getOrderId());
		assertEquals(3, anotherItemPrepTask.getNumOfItems());
		assertEquals(1, anotherItemPrepTask.getDay());
	}

}

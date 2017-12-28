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

		ItemPrepTask itemPrepTask = new ItemPrepTask();
		itemPrepTask.fromJSONObject(jsonObject);

		assertEquals("Bread", itemPrepTask.getProductId());
		assertEquals(2, itemPrepTask.getItemPrepTime());
		assertEquals(2, itemPrepTask.getReleaseDate());
		assertEquals(14, itemPrepTask.getDueDate());
		assertEquals("order-001", itemPrepTask.getOrderId());
		assertEquals(5, itemPrepTask.getNumOfItems());

		JSONObject jsonObjectFromItemPrepTask = new JSONObject();
		jsonObjectFromItemPrepTask = itemPrepTask.toJSONObject();

		assertEquals(6, jsonObjectFromItemPrepTask.length());
		assertEquals("Bread", jsonObjectFromItemPrepTask.get("product_id"));
		assertEquals(2l, jsonObjectFromItemPrepTask.get("item_prep_time"));
		assertEquals(2l, jsonObjectFromItemPrepTask.get("release_date"));
		assertEquals(14l, jsonObjectFromItemPrepTask.get("due_date"));
		assertEquals("order-001", jsonObjectFromItemPrepTask.get("order_id"));
		assertEquals(5, jsonObjectFromItemPrepTask.get("num_of_items"));
	}

}

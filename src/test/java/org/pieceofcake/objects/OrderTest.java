package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class OrderTest {

	private Order order;

	@Before
	public void prepareOrder() {
		JSONObject jsonOrder = new JSONObject();
		Date orderDate = new Date(1, 6, 0, 0);
		Date dueDate = new Date(2, 19, 0, 0);
		jsonOrder.put("order_date", orderDate.toJSONObject());
		jsonOrder.put("delivery_date", dueDate.toJSONObject());
		jsonOrder.put("guid", "order-001");
		jsonOrder.put("customer_id", "customer-001");
		JSONObject products = new JSONObject();
		products.put("Brezel", 7);
		products.put("Bread", 16);
		jsonOrder.put("products", products);
		Location location = new Location(1, 2);
		jsonOrder.put("location", location.toJSONObject());
		order = new Order(jsonOrder);
	}

	@Test
	public void testFromJSONObject() {
		assertEquals("order-001", order.getGuiId());
		assertEquals("customer-001", order.getCustomerId());
		assertEquals(new Date(1, 6, 0, 0), order.getOrderDate());
		assertEquals(new Date(2, 19, 0, 0), order.getDueDate());
		String[] productIds = order.getProductIds();
		assertEquals(2, productIds.length);
		assertEquals("Brezel", productIds[0]);
		assertEquals("Bread", productIds[1]);
		int[] productAmounts = order.getProductAmounts();
		assertEquals(2, productAmounts.length);
		assertEquals(7, productAmounts[0]);
		assertEquals(16, productAmounts[1]);
		assertEquals(new Location(1, 2), order.getLocation());
	}

	@Test
	public void testToJSONObject() {
		JSONObject jsonOrder = order.toJSONObject();
		assertEquals("order-001", jsonOrder.getString("guid"));
		assertEquals("customer-001", jsonOrder.getString("customer_id"));
		assertEquals(new Date(1, 6, 0, 0), new Date(jsonOrder.getJSONObject("order_date")));
		assertEquals(new Date(2, 19, 0, 0), new Date(jsonOrder.getJSONObject("delivery_date")));
		JSONObject products = jsonOrder.getJSONObject("products");
		assertEquals(2, products.length());
		assertTrue(products.has("Brezel"));
		assertEquals(7, products.getInt("Brezel"));
		assertTrue(products.has("Bread"));
		assertEquals(16, products.getInt("Bread"));
		Location location = new Location();
		location.fromJSONObject(jsonOrder.getJSONObject("location"));
		assertEquals(new Location(1, 2), new Location(location.getX(), location.getY()));
	}

	@Test
	public void testToString() {
		String orderString = "Order order-001 from customer customer-001\n"
				+ "OrderDate (in hours): 01:06:00:00\nDueDate (in hours): 02:19:00:00\n"
				+ "Products: (Brezel, 7), (Bread, 16)";
		assertEquals(orderString, order.toString());
	}

}

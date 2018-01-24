package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;

public class CookBookTest {
	
	public List<Order> prepareOrderListForTests() {
		List<Order> orders = new LinkedList<Order>();
		JSONObject jsonOrder1 = new JSONObject();
		Date orderDate = new Date(1, 6, 0, 0);
		Date dueDate = new Date(2, 19, 0, 0);
		jsonOrder1.put("order_date", orderDate.toJSONObject());
		jsonOrder1.put("delivery_date", dueDate.toJSONObject());
		jsonOrder1.put("guid", "order-001");
		jsonOrder1.put("customer_id", "customer-001");
		JSONObject products = new JSONObject();
		products.put("Cake", 7);
		products.put("Bread", 16);
		jsonOrder1.put("products", products);
		Location location = new Location(1, 2);
		jsonOrder1.put("location", location.toJSONObject());
		
		JSONObject jsonOrder2 = new JSONObject();
		Date orderDate2 = new Date(1, 6, 0, 0);
		Date dueDate2 = new Date(2, 19, 0, 0);
		jsonOrder2.put("order_date", orderDate2.toJSONObject());
		jsonOrder2.put("delivery_date", dueDate2.toJSONObject());
		jsonOrder2.put("guid", "order-001");
		jsonOrder2.put("customer_id", "customer-001");
		JSONObject products2 = new JSONObject();
		products2.put("Pie", 5);
		products2.put("Donut", 5);
		jsonOrder2.put("products", products2);
		Location location2 = new Location(1, 2);
		jsonOrder2.put("location", location2.toJSONObject());
		
		JSONObject jsonOrder3 = new JSONObject();
		Date orderDate3 = new Date(1, 6, 0, 0);
		Date dueDate3 = new Date(2, 19, 0, 0);
		jsonOrder3.put("order_date", orderDate3.toJSONObject());
		jsonOrder3.put("delivery_date", dueDate3.toJSONObject());
		jsonOrder3.put("guid", "order-001");
		jsonOrder3.put("customer_id", "customer-001");
		JSONObject products3 = new JSONObject();
		products3.put("Brezel", 7);
		products3.put("Bread", 16);
		jsonOrder3.put("products", products3);
		Location location3 = new Location(1, 2);
		jsonOrder3.put("location", location3.toJSONObject());
		
		orders.add(new Order(jsonOrder1));
		orders.add(new Order(jsonOrder2));
		orders.add(new Order(jsonOrder3));
		
		return orders;
}
	
	public List<Product> prepareProductListForTests() {
		List<Product> products = new LinkedList<>();
		String jsonProduct1 = "{\"boxing_temp\": 6,\"sales_price\": 14.1,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Bread\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 7.1}";
		String jsonProduct2 = "{\"boxing_temp\": 6,\"sales_price\": 20.2,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Cake\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 7.1}";
		String jsonProduct3 = "{\"boxing_temp\": 6,\"sales_price\": 4.3,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Pie\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 7.1}";
		String jsonProduct4 = "{\"boxing_temp\": 6,\"sales_price\": 5.2,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Donut\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 7.1}";

		products.add(new Product(jsonProduct1));
		products.add(new Product(jsonProduct2));
		products.add(new Product(jsonProduct3));
		products.add(new Product(jsonProduct4));
		return products;
	}
	
	@Test
	public void bakeryTest() {		
		CookBook cookBook = new CookBook();
		List<Order> orders = prepareOrderListForTests();
		List<Product> products = prepareProductListForTests();

		assertNull(cookBook.getProduct("testProduct"));
		assertNull(cookBook.getSalesPrice(orders.get(0)));

		for (Product product : products) {
			cookBook.addProduct(product);
		}
		
		assertEquals(367.0, cookBook.getSalesPrice(orders.get(0)), 1e-10);
		assertEquals(163.3, cookBook.getProductionPrice(orders.get(0)), 1e-10);
		assertEquals(47.5, cookBook.getSalesPrice(orders.get(1)), 1e-10);
		assertNull(cookBook.getSalesPrice(orders.get(2)));
		assertNull(cookBook.getProductionPrice(orders.get(2)));
		
		assertNull(cookBook.getProduct("nothing"));
		assertEquals("Pie", cookBook.getProduct("Pie").getId());

}

}

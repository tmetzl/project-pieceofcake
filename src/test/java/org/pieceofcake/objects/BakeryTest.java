package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.config.Topic;
import org.pieceofcake.interfaces.BakeryObserver;
import org.pieceofcake.objects.Bakery;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.Product;

public class BakeryTest {

	private List<Order> orders;

	@Before
	public void prepareOrder() {
		JSONObject jsonOrder1 = new JSONObject();
		Date orderDate1 = new Date(1, 6, 0, 0);
		Date dueDate1 = new Date(2, 19, 0, 0);
		jsonOrder1.put("order_date", orderDate1.toJSONObject());
		jsonOrder1.put("delivery_date", dueDate1.toJSONObject());
		jsonOrder1.put("guid", "order-003");
		jsonOrder1.put("customer_id", "customer-001");
		JSONObject products1 = new JSONObject();
		products1.put("Brezel", 5);
		products1.put("Bread", 11);
		jsonOrder1.put("products", products1);

		JSONObject jsonOrder2 = new JSONObject();
		Date orderDate2 = new Date(1, 4, 0, 0);
		Date dueDate2 = new Date(1, 17, 0, 0);
		jsonOrder2.put("order_date", orderDate2.toJSONObject());
		jsonOrder2.put("delivery_date", dueDate2.toJSONObject());
		jsonOrder2.put("guid", "order-002");
		jsonOrder2.put("customer_id", "customer-003");
		JSONObject products2 = new JSONObject();
		products2.put("Donut", 11);
		products2.put("Bread", 16);
		jsonOrder2.put("products", products2);

		JSONObject jsonOrder3 = new JSONObject();
		Date orderDate3 = new Date(0, 17, 0, 0);
		Date dueDate3 = new Date(2, 14, 0, 0);
		jsonOrder3.put("order_date", orderDate3.toJSONObject());
		jsonOrder3.put("delivery_date", dueDate3.toJSONObject());
		jsonOrder3.put("guid", "order-001");
		jsonOrder3.put("customer_id", "customer-002");
		JSONObject products3 = new JSONObject();
		products3.put("Cake", 6);
		products3.put("Pie", 3);
		jsonOrder3.put("products", products3);

		orders = new LinkedList<>();
		orders.add(new Order(jsonOrder1));
		orders.add(new Order(jsonOrder2));
		orders.add(new Order(jsonOrder3));
	}

	public List<Product> prepareProductListForTests() {
		List<Product> products = new LinkedList<Product>();
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
		Bakery bakery = new Bakery("bakery-001", "TestBakery", new Location(10, 17));
		List<Product> products = prepareProductListForTests();

		assertNull(bakery.getOrdersOfDay(1));
		assertNull(bakery.getProductByName("testProduct"));
		assertNull(bakery.getPrice(orders.get(0)));
		for (Order order : orders) {
			bakery.addOrder(order);
		}
		for (Product product : products) {
			bakery.addProduct(product);
		}
		List<Order> ordersOfDayTwo = bakery.getOrdersOfDay(2);
		assertEquals(2, ordersOfDayTwo.size());
		assertEquals("order-001", ordersOfDayTwo.get(0).getGuiId());
		assertEquals("order-003", ordersOfDayTwo.get(1).getGuiId());
		List<Order> ordersOfDayOne = bakery.getOrdersOfDay(1);
		assertEquals(1, ordersOfDayOne.size());
		assertEquals("order-002", ordersOfDayOne.get(0).getGuiId());
		assertNull(bakery.getPrice(orders.get(0)));
		assertEquals(new Double(282.8), bakery.getPrice(orders.get(1)));
		assertEquals(new Double(134.1), bakery.getPrice(orders.get(2)));

	}

	@Test
	public void bakeryDoughListTest() {
		Bakery bakery = new Bakery("bakery-002", "TestBakery2", new Location(12, 12));
		assertFalse(bakery.isDoughInStock("Bread"));
		bakery.updateDoughList("Bread");
		assertTrue(bakery.isDoughInStock("Bread"));
		bakery.newDay();
		assertFalse(bakery.isDoughInStock("Bread"));
	}

	@Test
	public void bakeryGetterTest() {
		Bakery bakery = new Bakery("bakery-001", "TestBakery", new Location(10.1, 17.2));
		assertEquals("bakery-001", bakery.getGuiId());
		assertEquals("TestBakery", bakery.getName());
		assertEquals(10.1, bakery.getLocation().getX(), 1e-10);
		assertEquals(17.2, bakery.getLocation().getY(), 1e-10);
	}

	@Test
	public void bakeryObserverTest() {
		TestObserver doughListObserver1 = new TestObserver();
		TestObserver doughListObserver2 = new TestObserver();
		Bakery bakery = new Bakery("bakery-001", "TestBakery", new Location(10.1, 17.2));
		bakery.registerObserver(doughListObserver1, Topic.DOUGH);
		bakery.registerObserver(doughListObserver2, Topic.DOUGH);
		assertFalse(doughListObserver1.notified);
		assertFalse(doughListObserver2.notified);
		bakery.newDay();
		assertTrue(doughListObserver1.notified);
		assertTrue(doughListObserver2.notified);
	}

	private class TestObserver implements BakeryObserver {

		boolean notified = false;

		@Override
		public void notifyObserver(String topic) {
			notified = true;
		}

	}

}

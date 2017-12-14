package maas.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import maas.config.Topic;
import maas.interfaces.BakeryObserver;
import maas.objects.Order;

public class BakeryTest {
		
	public List<Order> prepareOrderListForTests() {
		List<Order> orders = new LinkedList<Order>();
		String jsonOrder1 = "{\"order_date\": {\"day\": 1,\"hour\": 6},"
				+ "\"guid\": \"order-003\",\"products\": {\"Brezel\": 5,\"Bread\": 11},"
				+ "\"customer_id\": \"customer-001\"," + "\"delivery_date\": {\"day\": 2,\"hour\": 19}}";
		String jsonOrder2 = "{\"order_date\": {\"day\": 1,\"hour\": 4},"
				+ "\"guid\": \"order-002\",\"products\": {\"Donut\": 11,\"Bread\": 16},"
				+ "\"customer_id\": \"customer-003\"," + "\"delivery_date\": {\"day\": 1,\"hour\": 17}}";
		String jsonOrder3 = "{\"order_date\": {\"day\": 0,\"hour\": 17},"
				+ "\"guid\": \"order-001\",\"products\": {\"Cake\": 6,\"Pie\": 3},"
				+ "\"customer_id\": \"customer-002\"," + "\"delivery_date\": {\"day\": 2,\"hour\": 14}}";

		orders.add(new Order(jsonOrder1));
		orders.add(new Order(jsonOrder2));
		orders.add(new Order(jsonOrder3));
		return orders;
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
		Bakery bakery = new Bakery("bakery-001", "TestBakery", 10, 17);
		List<Order> orders = prepareOrderListForTests();
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
		Bakery bakery = new Bakery("bakery-002", "TestBakery2", 12, 12);
		assertFalse(bakery.isDoughInStock("Bread"));
		bakery.updateDoughList("Bread");
		assertTrue(bakery.isDoughInStock("Bread"));
		bakery.newDay();
		assertFalse(bakery.isDoughInStock("Bread"));
	}
	
	@Test
	public void bakeryGetterTest() {
		Bakery bakery = new Bakery("bakery-001", "TestBakery", 10.1, 17.2);
		assertEquals("bakery-001", bakery.getGuiId());
		assertEquals("TestBakery", bakery.getName());
		assertEquals(10.1, bakery.getLocationX(), 1e-10);
		assertEquals(17.2, bakery.getLocationY(), 1e-10);
	}
	
	@Test
	public void bakeryObserverTest() {
		TestObserver observer = new TestObserver();
		Bakery bakery = new Bakery("bakery-001", "TestBakery", 10.1, 17.2);
		bakery.registerObserver(observer, Topic.DOUGH);
		assertFalse(observer.notified);
		bakery.newDay();
		assertTrue(observer.notified);		
	}
	
	
	private class TestObserver implements BakeryObserver {
		
		boolean notified = false;

		@Override
		public void notifyObserver(String topic) {
			notified = true;			
		}
		
	}
	
	

}

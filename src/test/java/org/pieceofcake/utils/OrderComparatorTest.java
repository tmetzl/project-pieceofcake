package org.pieceofcake.utils;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Order;

public class OrderComparatorTest {

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
		JSONObject products = new JSONObject();
		products.put("Brezel", 6);
		products.put("Bread", 16);
		jsonOrder1.put("products", products);
		
		JSONObject jsonOrder2 = new JSONObject();
		Date orderDate2 = new Date(1, 4, 0, 0);
		Date dueDate2 = new Date(1, 17, 0, 0);
		jsonOrder2.put("order_date", orderDate2.toJSONObject());
		jsonOrder2.put("delivery_date", dueDate2.toJSONObject());
		jsonOrder2.put("guid", "order-002");
		jsonOrder2.put("customer_id", "customer-003");
		jsonOrder2.put("products", products);	
		
		JSONObject jsonOrder3 = new JSONObject();
		Date orderDate3 = new Date(0, 17, 0, 0);
		Date dueDate3 = new Date(2, 14, 0, 0);
		jsonOrder3.put("order_date", orderDate3.toJSONObject());
		jsonOrder3.put("delivery_date", dueDate3.toJSONObject());
		jsonOrder3.put("guid", "order-001");
		jsonOrder3.put("customer_id", "customer-002");
		jsonOrder3.put("products", products);
		
		orders = new LinkedList<>();
		orders.add(new Order(jsonOrder1));
		orders.add(new Order(jsonOrder2));
		orders.add(new Order(jsonOrder3));
	}
	
	@Test
	public void testOrderDueDateComparator() {
		Collections.sort(orders, new OrderDueDateComparator());
		
		String order1Guid = orders.get(0).getGuiId();
		String order2Guid = orders.get(1).getGuiId();
		String order3Guid = orders.get(2).getGuiId();
		
		assertTrue("order-002".equals(order1Guid));
		assertTrue("order-001".equals(order2Guid));
		assertTrue("order-003".equals(order3Guid));
	}
	
	@Test
	public void testOrderDateComparator() {
		Collections.sort(orders, new OrderDateComparator());
		
		String order1Guid = orders.get(0).getGuiId();
		String order2Guid = orders.get(1).getGuiId();
		String order3Guid = orders.get(2).getGuiId();
		
		assertTrue("order-001".equals(order1Guid));
		assertTrue("order-002".equals(order2Guid));
		assertTrue("order-003".equals(order3Guid));
	}
	
	@Test
	public void testOrderGuiIdComparator() {
		Collections.sort(orders, new OrderGuiIdComparator());
		
		String order1Guid = orders.get(0).getGuiId();
		String order2Guid = orders.get(1).getGuiId();
		String order3Guid = orders.get(2).getGuiId();
		
		assertTrue("order-001".equals(order1Guid));
		assertTrue("order-002".equals(order2Guid));
		assertTrue("order-003".equals(order3Guid));
	}
	
	@Test
	public void testOrderCustomerIdComparator() {
		Collections.sort(orders, new OrderCustomerIdComparator());
		
		String order1Guid = orders.get(0).getGuiId();
		String order2Guid = orders.get(1).getGuiId();
		String order3Guid = orders.get(2).getGuiId();
		
		assertTrue("order-003".equals(order1Guid));
		assertTrue("order-001".equals(order2Guid));
		assertTrue("order-002".equals(order3Guid));
	}


}

package maas.test;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import maas.objects.Order;
import maas.utils.OrderCustomerIdComparator;
import maas.utils.OrderDateComparator;
import maas.utils.OrderDueDateComparator;
import maas.utils.OrderGuiIdComparator;

public class OrderComparatorTest {

	public List<Order> prepareOrderListForTests() {
		List<Order> orders = new LinkedList<Order>();
		String jsonOrder1 = "{\"order_date\": {\"day\": 1,\"hour\": 6},"
				+ "\"guid\": \"order-003\",\"products\": {\"Brezel\": 6,\"Bread\": 16},"
				+ "\"customer_id\": \"customer-001\"," + "\"delivery_date\": {\"day\": 2,\"hour\": 19}}";
		String jsonOrder2 = "{\"order_date\": {\"day\": 1,\"hour\": 4},"
				+ "\"guid\": \"order-002\",\"products\": {\"Brezel\": 6,\"Bread\": 16},"
				+ "\"customer_id\": \"customer-003\"," + "\"delivery_date\": {\"day\": 1,\"hour\": 17}}";
		String jsonOrder3 = "{\"order_date\": {\"day\": 0,\"hour\": 17},"
				+ "\"guid\": \"order-001\",\"products\": {\"Brezel\": 6,\"Bread\": 16},"
				+ "\"customer_id\": \"customer-002\"," + "\"delivery_date\": {\"day\": 2,\"hour\": 14}}";

		orders.add(new Order(jsonOrder1));
		orders.add(new Order(jsonOrder2));
		orders.add(new Order(jsonOrder3));
		return orders;
	}
	
	@Test
	public void testOrderDueDateComparator() {
		List<Order> orders = this.prepareOrderListForTests();
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
		List<Order> orders = this.prepareOrderListForTests();
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
		List<Order> orders = this.prepareOrderListForTests();
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
		List<Order> orders = this.prepareOrderListForTests();
		Collections.sort(orders, new OrderCustomerIdComparator());
		
		String order1Guid = orders.get(0).getGuiId();
		String order2Guid = orders.get(1).getGuiId();
		String order3Guid = orders.get(2).getGuiId();
		
		assertTrue("order-003".equals(order1Guid));
		assertTrue("order-001".equals(order2Guid));
		assertTrue("order-002".equals(order3Guid));
	}


}

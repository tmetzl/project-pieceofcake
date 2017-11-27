package maas.objects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import maas.objects.Order;

public class OrderTest {

	@Test
	public void jsonParseToOrder() {
		String jsonOrder = "{\"order_date\": {\"day\": 1,\"hour\": 6},"
				+ "\"guid\": \"order-001\",\"products\": {\"Brezel\": 7,\"Bread\": 16},"
				+ "\"customer_id\": \"customer-001\"," + "\"delivery_date\": {\"day\": 2,\"hour\": 19}}";
		Order order = new Order(jsonOrder);

		assertEquals("order-001", order.getGuiId());
		assertEquals("customer-001", order.getCustomerId());
		assertEquals(30, order.getOrderDate());
		assertEquals(67, order.getDueDate());
		String[] productIds = order.getProductIds();
		assertEquals(2, productIds.length);
		assertEquals("Brezel", productIds[0]);
		assertEquals("Bread", productIds[1]);
		int[] productAmounts = order.getProductAmounts();
		assertEquals(2, productAmounts.length);
		assertEquals(7, productAmounts[0]);
		assertEquals(16, productAmounts[1]);

	}

}

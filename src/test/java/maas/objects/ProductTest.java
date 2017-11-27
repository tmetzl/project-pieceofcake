package maas.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import maas.objects.Product;

public class ProductTest {

	@Test
	public void jsonParseToProduct() {
		String jsonProduct = "{\"boxing_temp\": 6,\"sales_price\": 14,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Bread\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 7}";
		Product product = new Product(jsonProduct);

		assertEquals(6, product.getBoxingTemp());
		assertEquals(14, product.getSalesPrice());
		assertEquals(6, product.getBreadsPerOven());
		assertEquals(6, product.getBreadsPerBox());
		assertEquals(11, product.getItemPrepTime());
		assertEquals(1, product.getDoughPrepTime());
		assertEquals(94, product.getBakingTemp());
		assertEquals(1, product.getCoolingRate());
		assertEquals(11, product.getBakingTime());
		assertEquals(9, product.getDoughRestingTime());
		assertEquals(7, product.getProductionCost());
		assertTrue("Bread".equals(product.getId()));

	}

}

package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProductTest {

	@Test
	public void jsonParseToProduct() {
		String jsonProduct = "{\"boxing_temp\": 6,\"sales_price\": 3.29,\"breads_per_oven\": 6,"
				+ "\"breads_per_box\": 6,\"item_prep_time\": 11,\"dough_prep_time\": 1,"
				+ "\"baking_temp\": 94,\"cooling_rate\": 1,\"guid\": \"Bread\","
				+ "\"baking_time\": 11,\"resting_time\": 9,\"production_cost\": 1.43}";
		Product product = new Product(jsonProduct);

		assertEquals(6, product.getBoxingTemp());
		assertEquals(3.29, product.getSalesPrice(), 1e-10);
		assertEquals(6, product.getBreadsPerOven());
		assertEquals(6, product.getBreadsPerBox());
		assertEquals(11, product.getItemPrepTime());
		assertEquals(1, product.getDoughPrepTime());
		assertEquals(94, product.getBakingTemp());
		assertEquals(1, product.getCoolingRate());
		assertEquals(11, product.getBakingTime());
		assertEquals(9, product.getDoughRestingTime());
		assertEquals(1.43, product.getProductionCost(), 1e-10);
		assertTrue("Bread".equals(product.getId()));

		String toString = "Product Bread\nDough preparation time: 1\nDough resting time: 9\nItem preparation time: 11"
				+ "\nBreads per oven: 6\nBaking time: 11\nBaking temp: 94\nCooling rate: 1\nBoxing temperature: 6"
				+ "\nBreads per box: 6\nProduction cost: 1.43\nSales price: 3.29";
		assertEquals(toString, product.toString());

	}

}

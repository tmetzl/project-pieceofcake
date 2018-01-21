package org.pieceofcake.objects;

import java.util.HashMap;
import java.util.Map;

public class CookBook {
	
	private Map<String, Product> products;
	
	public CookBook() {
		this.products = new HashMap<>();
	}
	
	public void addProduct(Product product) {
		products.put(product.getId(), product);
	}
	
	public Product getProduct(String productId) {
		return products.get(productId);
	}
	
	public Double getSalesPrice(Order order) {
		double price = 0;

		String[] productIds = order.getProductIds();
		int[] productAmount = order.getProductAmounts();

		for (int i = 0; i < productIds.length; i++) {
			Product product = products.get(productIds[i]);
			if (product == null) {
				return null;
			}
			price += productAmount[i] * product.getSalesPrice();
		}

		return price;
	}
	
	public Double getProductionPrice(Order order) {
		double price = 0;

		String[] productIds = order.getProductIds();
		int[] productAmount = order.getProductAmounts();

		for (int i = 0; i < productIds.length; i++) {
			Product product = products.get(productIds[i]);
			if (product == null) {
				return null;
			}
			price += productAmount[i] * product.getProductionCost();
		}

		return price;
	}

}

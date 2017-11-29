package maas.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import maas.utils.OrderDueDateComparator;

public class Bakery {

	private String guiId;
	private String name;
	private int locationX;
	private int locationY;

	private List<Order> orders;
	private List<Order> ordersInProcess;
	private Map<String, Product> cookBook;

	public Bakery(String guiId, String name, int locationX, int locationY) {
		this.guiId = guiId;
		this.name = name;
		this.locationX = locationX;
		this.locationY = locationY;

		this.orders = new LinkedList<>();
		this.ordersInProcess = new LinkedList<>();
		this.cookBook = new HashMap<>();
	}

	public Product getProductByName(String productName) {
		if (this.cookBook.containsKey(productName)) {
			return this.cookBook.get(productName);
		}
		return null;
	}
	
	public Double getPrice(Order order) {
		double price = 0;
		
		String[] productIds = order.getProductIds();
		int[] productAmount = order.getProductAmounts();
		
		for (int i=0;i<productIds.length;i++) {
			Product product = getProductByName(productIds[i]);
			if (product == null) {
				return null;
			}
			price += productAmount[i]*product.getSalesPrice();
		}
		
		return price;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
		Collections.sort(this.orders, new OrderDueDateComparator());
	}

	public void addProduct(Product product) {
		String productId = product.getId();
		this.cookBook.put(productId, product);
	}

	public Order getOrder() {
		if (!this.orders.isEmpty()) {
			Order order = this.orders.get(0);
			this.ordersInProcess.add(order);
			this.orders.remove(0);
			return order;
		}
		return null;
	}

}

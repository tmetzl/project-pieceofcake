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

	private Map<Integer, List<Order>> orderDayMap;
	private List<Order> ordersInProcess;
	private Map<String, Product> cookBook;
	private Map<String, Boolean> doughInStock;

	public Bakery(String guiId, String name, int locationX, int locationY) {
		this.guiId = guiId;
		this.name = name;
		this.locationX = locationX;
		this.locationY = locationY;

		this.ordersInProcess = new LinkedList<>();
		this.cookBook = new HashMap<>();
		this.orderDayMap = new HashMap<>();
		this.doughInStock = new HashMap<>();
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

		for (int i = 0; i < productIds.length; i++) {
			Product product = getProductByName(productIds[i]);
			if (product == null) {
				return null;
			}
			price += productAmount[i] * product.getSalesPrice();
		}

		return price;
	}

	public void addOrder(Order order) {
		int day = order.getDueDate() / 24;
		List<Order> ordersPerDay = orderDayMap.get(day);
		if (ordersPerDay == null) {
			ordersPerDay = new LinkedList<>();
		}
		ordersPerDay.add(order);
		Collections.sort(ordersPerDay, new OrderDueDateComparator());
		orderDayMap.put(day, ordersPerDay);
	}

	public void addProduct(Product product) {
		String productId = product.getId();
		this.cookBook.put(productId, product);
	}

	public List<Order> getOrdersOfDay(int day) {
		return orderDayMap.get(day);
	}

	public boolean isDoughInStock(String productName) {
		Boolean answer = doughInStock.get(productName);
		if (answer != null) {
			return answer;
		}
		return false;
	}

	public void updateDoughList(String productName) {
		doughInStock.put(productName, true);
	}
}

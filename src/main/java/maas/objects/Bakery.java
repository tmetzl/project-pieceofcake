package maas.objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import maas.config.Topic;
import maas.interfaces.BakeryObservable;
import maas.interfaces.BakeryObserver;
import maas.interfaces.Localizable;
import maas.utils.OrderDueDateComparator;

public class Bakery implements Serializable, BakeryObservable, Localizable {

	private static final long serialVersionUID = 8794276456115280744L;

	private String guiId;
	private String name;
	private Location location;

	private Map<Integer, List<Order>> orderDayMap;
	private Map<String, Product> cookBook;
	private Map<String, Boolean> doughInStock;
	private transient Map<String, List<BakeryObserver>> observers;

	public Bakery(String guiId, String name, Location location) {
		this.guiId = guiId;
		this.name = name;
		this.location = location;

		this.cookBook = new HashMap<>();
		this.orderDayMap = new HashMap<>();
		this.doughInStock = new HashMap<>();
		this.observers = new HashMap<>();
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
		notifyObservers(Topic.DAILY_ORDERS);
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
		notifyObservers(Topic.DOUGH);
	}

	@Override
	public void registerObserver(BakeryObserver observer, String topic) {
		List<BakeryObserver> observerList = observers.get(topic);
		if (observerList == null) {
			observerList = new LinkedList<>();
		}
		observerList.add(observer);
		observers.put(topic, observerList);
	}

	@Override
	public void notifyObservers(String topic) {
		List<BakeryObserver> observerList = observers.get(topic);
		if (observerList != null) {
			for (BakeryObserver observer : observerList) {
				observer.notifyObserver(topic);
			}
		}
	}
	
	public void newDay() {
		doughInStock.clear();
		Set<String> topics = observers.keySet();
		for (String topic : topics) {
			notifyObservers(topic);
		}
	}

	public String getGuiId() {
		return guiId;
	}

	public String getName() {
		return name;
	}

	@Override
	public double getLocationX() {
		return location.getLocationX();
	}

	@Override
	public double getLocationY() {
		return location.getLocationY();
	}
}

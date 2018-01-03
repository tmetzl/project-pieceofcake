package maas.objects;

import java.util.LinkedList;
import java.util.List;

import maas.tasks.BakingTask;
import maas.tasks.DeliveryTask;
import maas.tasks.ItemPrepTask;
import maas.tasks.KneadingTask;

public class TaskCreator {

	private Bakery bakery;

	public TaskCreator(Bakery bakery) {
		this.bakery = bakery;
	}

//	public List<KneadingTask> getKneadinTasks(Order order) {
//
//		List<KneadingTask> kneadingTasks = new LinkedList<>();
//		String[] listOfProducts = order.getProductIds();
//		String orderId = order.getGuiId();
//
//		for (String productId : listOfProducts) {
//
//			Product product = bakery.getProductByName(productId);
//
//			long kneadingTime = product.getDoughPrepTime();
//			long restingTime = product.getDoughRestingTime();
//			long dueDate = order.getDueDate();
//			long dueHour = dueDate % 24;
//			// day is the due date day or the order placement day???
//			int day = (int) (dueDate / 24);
//			long releaseHour = order.getOrderDate() % 24;
//
//			KneadingTask task = new KneadingTask(day, kneadingTime, restingTime, dueHour, releaseHour, orderId,
//					productId);
//			kneadingTasks.add(task);
//
//		}
//		return kneadingTasks;
//	}
//
//	public List<ItemPrepTask> getItemPrepTasks(Order order) {
//
//		List<ItemPrepTask> itemPrepTasks = new LinkedList<>();
//		String[] listOfProducts = order.getProductIds();
//		String orderId = order.getGuiId();
//		int[] productAmounts = order.getProductAmounts();
//
//		for (int i = 0; i < listOfProducts.length; i++) {
//
//			Product product = bakery.getProductByName(listOfProducts[i]);
//
//			int numOfItems = productAmounts[i];
//			long itemPrepTime = product.getItemPrepTime();
//			long dueDate = order.getDueDate();
//			long dueHour = dueDate % 24;
//			// day is the due date day or the order placement day???
//			int day = (int) (dueDate / 24);
//			long releaseHour = order.getOrderDate() % 24;
//
//			ItemPrepTask task = new ItemPrepTask(day, numOfItems, itemPrepTime, dueHour, releaseHour, orderId,
//					listOfProducts[i]);
//			itemPrepTasks.add(task);
//
//		}
//		return itemPrepTasks;
//	}
//
//	public List<BakingTask> getBakingTasks(Order order) {
//
//		List<BakingTask> bakingTasks = new LinkedList<>();
//		String[] listOfProducts = order.getProductIds();
//		String orderId = order.getGuiId();
//		int[] productAmounts = order.getProductAmounts();
//
//		for (int i = 0; i < listOfProducts.length; i++) {
//			Product product = bakery.getProductByName(listOfProducts[i]);
//
//			int numOfItems = productAmounts[i];
//			long bakingTime = product.getBakingTime();
//			long bakingTemp = product.getBakingTemp();
//			double coolingRate = product.getCoolingRate();
//			long dueDate = order.getDueDate();
//			long dueHour = dueDate % 24;
//			// day is the due date day or the order placement day???
//			int day = (int) (dueDate / 24);
//			long releaseHour = order.getOrderDate() % 24;
//
//			BakingTask task = new BakingTask(day, bakingTime, bakingTemp, numOfItems, coolingRate, dueHour, releaseHour,
//					orderId, listOfProducts[i]);
//			bakingTasks.add(task);
//
//		}
//		return bakingTasks;
//	}
//
//	public List<DeliveryTask> getDeliveryTasks(Order order) {
//
//		List<DeliveryTask> deliveryTasks = new LinkedList<>();
//		String[] listOfProducts = order.getProductIds();
//		String orderId = order.getGuiId();
//		String customerId = order.getCustomerId();
//		int[] productAmounts = order.getProductAmounts();
//
//		for (int i = 0; i < listOfProducts.length; i++) {
//			Product product = bakery.getProductByName(listOfProducts[i]);
//
//			int numOfItems = productAmounts[i];
//			int itemsPerBox = product.getBreadsPerBox();
//			// round up ???
//			int numOfBoxes = (int) Math.ceil(numOfItems / 1.0 * itemsPerBox);
//			long dueDate = order.getDueDate();
//			long dueHour = dueDate % 24;
//			// day is the due date day or the order placement day???
//			int day = (int) (dueDate / 24);
//			long releaseHour = order.getOrderDate() % 24;
//
//			DeliveryTask task = new DeliveryTask(day, numOfBoxes, customerId, dueHour, releaseHour, orderId,
//					listOfProducts[i]);
//			deliveryTasks.add(task);
//
//		}
//		return deliveryTasks;
//	}
}

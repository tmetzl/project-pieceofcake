package org.pieceofcake.behaviours;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.TaskDescriptor;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.BakingTask;
import org.pieceofcake.tasks.CoolingTask;
import org.pieceofcake.tasks.DeliveryTask;
import org.pieceofcake.tasks.ItemPrepTask;
import org.pieceofcake.tasks.KneadingTask;
import org.pieceofcake.tasks.RestingTask;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class ScheduleOrder extends SequentialBehaviour {

	private static final long serialVersionUID = -2844996881242194690L;

	private OrderContract contract;
	private CookBook cookBook;
	private List<Behaviour> taskHandlers;
	private String bakeryName;

	public ScheduleOrder(OrderContract contract, CookBook cookBook, String bakeryName) {
		this.contract = contract;
		this.cookBook = cookBook;
		this.bakeryName = bakeryName;
		this.taskHandlers = new LinkedList<>();
		this.taskHandlers.add(new HandleTasks<>(contract, new KneadingTaskDescriptor()));
		this.taskHandlers.add(new HandleTasks<>(contract, new RestingTaskDescriptor()));
		this.taskHandlers.add(new HandleTasks<>(contract, new ItemPrepTaskDescriptor()));
		this.taskHandlers.add(new HandleTasks<>(contract, new BakingTaskDescriptor()));
		this.taskHandlers.add(new HandleTasks<>(contract, new CoolingTaskDescriptor()));
		this.taskHandlers.add(new HandleTasks<>(contract, new DeliveryTaskDescriptor()));

		this.addSubBehaviour(new Controller());
	}

	public Date getProductionDueDate() {
		Date dueDate = contract.getOrder().getDueDate();
		if (dueDate.compareTo(new Date(dueDate.getDay(), 12, 0, 0)) > 0) {
			dueDate = new Date(dueDate.getDay(), 12, 0, 0);
		}
		return dueDate;
	}

	private class Controller extends OneShotBehaviour {

		private static final long serialVersionUID = 8177316770094605092L;

		@Override
		public void action() {
			if (contract.hasFailed()) {
				ScheduleOrder.this.addSubBehaviour(new CancelOrderContract(contract));
			} else if (!taskHandlers.isEmpty()) {
				ScheduleOrder.this.addSubBehaviour(taskHandlers.remove(0));
				ScheduleOrder.this.addSubBehaviour(new Controller());
			}

		}

	}

	private class KneadingTaskDescriptor implements TaskDescriptor<KneadingTask> {

		private static final long serialVersionUID = 2539302258065744071L;

		@Override
		public List<KneadingTask> prepareTasks() {
			List<KneadingTask> kneadingTasks = new LinkedList<>();
			for (String product : contract.getOrder().getProductIds()) {
				KneadingTask task = new KneadingTask();
				task.setOrderId(contract.getOrder().getGuiId());
				task.setProductId(product);
				task.setNumOfItems(1);
				task.setDueDate(contract.getOrder().getDueDate());
				task.setKneadingTime(cookBook.getProduct(product).getDoughPrepTime());
				if (contract.getOrder().getOrderDate().getDay() < contract.getOrder().getDueDate().getDay()) {
					task.setReleaseDate(new Date(contract.getOrder().getDueDate().getDay(), 0, 0, 0));
				} else {
					task.setReleaseDate(contract.getOrder().getOrderDate());
				}
				kneadingTasks.add(task);
			}
			return kneadingTasks;
		}

		@Override
		public Date getDueDate() {
			return getProductionDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, KneadingTask task, OrderContract contract) {
			contract.addKneadingTask(agentId, task);
		}

		@Override
		public String getServiceType() {
			return Services.KNEAD;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.KNEAD;
		}

	}

	private class RestingTaskDescriptor implements TaskDescriptor<RestingTask> {

		private static final long serialVersionUID = 8517411323531517287L;

		@Override
		public List<RestingTask> prepareTasks() {
			List<RestingTask> restingTasks = new LinkedList<>();
			List<KneadingTask> scheduledKneadingTasks = contract.getKneadingTasks();
			for (KneadingTask kneadingTask : scheduledKneadingTasks) {
				RestingTask task = new RestingTask();
				task.setOrderId(kneadingTask.getOrderId());
				task.setNumOfItems(1);
				task.setProductId(kneadingTask.getProductId());
				task.setDueDate(contract.getOrder().getDueDate());
				task.setRestingTime(cookBook.getProduct(task.getProductId()).getDoughRestingTime());
				task.setReleaseDate(kneadingTask.getDueDate());
				restingTasks.add(task);
			}
			return restingTasks;
		}

		@Override
		public Date getDueDate() {
			return getProductionDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, RestingTask task, OrderContract contract) {
			contract.addRestingTask(agentId, task);

		}

		@Override
		public String getServiceType() {
			return Services.REST;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.REST;
		}

	}

	private class ItemPrepTaskDescriptor implements TaskDescriptor<ItemPrepTask> {

		private static final long serialVersionUID = -7640934465607940240L;

		@Override
		public List<ItemPrepTask> prepareTasks() {
			List<ItemPrepTask> itemPrepTasks = new LinkedList<>();
			Map<String, Integer> productAmounts = new HashMap<>();
			for (int i = 0; i < contract.getOrder().getProductIds().length; i++) {
				productAmounts.put(contract.getOrder().getProductIds()[i], contract.getOrder().getProductAmounts()[i]);
			}
			List<RestingTask> scheduledRestingTasks = contract.getRestingTasks();
			for (RestingTask restingTask : scheduledRestingTasks) {
				ItemPrepTask task = new ItemPrepTask();
				task.setOrderId(restingTask.getOrderId());
				task.setProductId(restingTask.getProductId());
				task.setNumOfItems(productAmounts.get(task.getProductId()));
				task.setDueDate(contract.getOrder().getDueDate());
				task.setItemPrepTime(cookBook.getProduct(task.getProductId()).getItemPrepTime());
				task.setReleaseDate(restingTask.getDueDate());
				itemPrepTasks.add(task);
			}
			return itemPrepTasks;
		}

		@Override
		public Date getDueDate() {
			return getProductionDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, ItemPrepTask task, OrderContract contract) {
			contract.addItemPrepTask(agentId, task);

		}

		@Override
		public String getServiceType() {
			return Services.PREP;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.PREP;
		}

	}

	private class BakingTaskDescriptor implements TaskDescriptor<BakingTask> {

		private static final long serialVersionUID = -9203853513681591829L;

		@Override
		public List<BakingTask> prepareTasks() {
			List<BakingTask> bakingTasks = new LinkedList<>();
			List<ItemPrepTask> scheduledItemPrepTasks = contract.getItemPrepTasks();
			for (ItemPrepTask itemPrepTask : scheduledItemPrepTasks) {
				BakingTask task = new BakingTask();
				task.setOrderId(itemPrepTask.getOrderId());
				task.setProductId(itemPrepTask.getProductId());
				task.setDueDate(contract.getOrder().getDueDate());
				task.setReleaseDate(itemPrepTask.getDueDate());
				task.setNumOfItems(itemPrepTask.getNumOfItems());
				task.setBakingTemperature(cookBook.getProduct(task.getProductId()).getBakingTemp());
				task.setBakingTime(cookBook.getProduct(task.getProductId()).getBakingTime());
				task.setItemPerTray(cookBook.getProduct(task.getProductId()).getBreadsPerOven());
				bakingTasks.add(task);
			}
			return bakingTasks;
		}

		@Override
		public Date getDueDate() {
			return getProductionDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, BakingTask task, OrderContract contract) {
			contract.addBakingTask(agentId, task);

		}

		@Override
		public String getServiceType() {
			return Services.BAKE;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.BAKE;
		}

	}

	private class CoolingTaskDescriptor implements TaskDescriptor<CoolingTask> {

		private static final long serialVersionUID = -1476933508291283837L;

		@Override
		public List<CoolingTask> prepareTasks() {
			List<CoolingTask> coolingTasks = new LinkedList<>();
			List<BakingTask> scheduledBakingTasks = contract.getBakingTasks();
			for (BakingTask bakingTask : scheduledBakingTasks) {
				CoolingTask task = new CoolingTask();
				task.setOrderId(bakingTask.getOrderId());
				task.setProductId(bakingTask.getProductId());
				task.setDueDate(contract.getOrder().getDueDate());
				task.setReleaseDate(bakingTask.getDueDate());
				task.setNumOfItems(bakingTask.getNumOfItems());
				task.setBakingTemperature(bakingTask.getBakingTemperature());
				task.setCoolingTimeFactor(cookBook.getProduct(task.getProductId()).getCoolingRate());
				coolingTasks.add(task);
			}
			return coolingTasks;
		}

		@Override
		public Date getDueDate() {
			return getProductionDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, CoolingTask task, OrderContract contract) {
			contract.addCoolingTask(agentId, task);

		}

		@Override
		public String getServiceType() {
			return Services.COOL;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.COOL;
		}

	}

	private class DeliveryTaskDescriptor implements TaskDescriptor<DeliveryTask> {

		private static final long serialVersionUID = -2012773224825103351L;

		@Override
		public List<DeliveryTask> prepareTasks() {
			List<DeliveryTask> deliveryTasks = new LinkedList<>();
			List<CoolingTask> scheduledCoolingTasks = contract.getCoolingTasks();
			for (CoolingTask coolingTask : scheduledCoolingTasks) {
				DeliveryTask task = new DeliveryTask();
				task.setOrderId(coolingTask.getOrderId());
				task.setProductId(coolingTask.getProductId());
				task.setDueDate(contract.getOrder().getDueDate());
				task.setReleaseDate(coolingTask.getDueDate());
				task.setNumOfItems(coolingTask.getNumOfItems());
				task.setLocation(contract.getOrder().getLocation());
				task.setItemPerBox(cookBook.getProduct(task.getProductId()).getBreadsPerBox());
				deliveryTasks.add(task);
			}
			return deliveryTasks;
		}

		@Override
		public Date getDueDate() {
			return contract.getOrder().getDueDate();
		}

		@Override
		public void addTaskToOrder(AID agentId, DeliveryTask task, OrderContract contract) {
			contract.addDeliveryTask(agentId, task);

		}

		@Override
		public String getServiceType() {
			return Services.DELIVERY;
		}

		@Override
		public String getBakeryName() {
			return bakeryName;
		}

		@Override
		public String getProtocol() {
			return Protocols.DELIVERY;
		}

	}

}

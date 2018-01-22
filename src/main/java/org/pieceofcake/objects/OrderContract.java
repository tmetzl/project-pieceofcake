package org.pieceofcake.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pieceofcake.tasks.*;

import jade.core.AID;

public class OrderContract implements Serializable {

	private static final long serialVersionUID = 5764464650645828722L;

	private Order order;
	private Map<AID, List<KneadingTask>> kneadingTaskMap;
	private Map<AID, List<RestingTask>> restingTaskMap;
	private Map<AID, List<ItemPrepTask>> itemPrepTaskMap;
	private Map<AID, List<BakingTask>> bakingTaskMap;
	private Map<AID, List<CoolingTask>> coolingTaskMap;
	private Map<AID, List<DeliveryTask>> deliveryTaskMap;
	private boolean hasFailed;
	private AID customerAgentId;

	public OrderContract(Order order, AID customerAgentId) {
		this.order = order;
		this.kneadingTaskMap = new HashMap<>();
		this.restingTaskMap = new HashMap<>();
		this.itemPrepTaskMap = new HashMap<>();
		this.bakingTaskMap = new HashMap<>();
		this.coolingTaskMap = new HashMap<>();
		this.deliveryTaskMap = new HashMap<>();
		this.hasFailed = false;
		this.customerAgentId = customerAgentId;
	}

	public Order getOrder() {
		return order;
	}
	
	public boolean hasFailed() {
		return hasFailed;
	}
	
	public void setFailed(boolean hasFailed) {
		this.hasFailed = hasFailed;
	}

	private <T extends Task> void addTask(AID agentId, T task, Map<AID, List<T>> taskMap) {
		List<T> existingTasks = taskMap.get(agentId);
		if (existingTasks == null) {
			existingTasks = new LinkedList<>();
		}
		existingTasks.add(task);
		taskMap.put(agentId, existingTasks);
	}

	private <T extends Task> List<T> getTasks(Map<AID, List<T>> taskMap) {
		List<T> tasks = new LinkedList<>();
		for (List<T> existingTasks : taskMap.values()) {
			tasks.addAll(existingTasks);
		}
		return tasks;
	}

	public List<AID> getAgents() {
		List<AID> agents = new LinkedList<>();
		agents.addAll(kneadingTaskMap.keySet());
		agents.addAll(restingTaskMap.keySet());
		agents.addAll(itemPrepTaskMap.keySet());
		agents.addAll(bakingTaskMap.keySet());
		agents.addAll(coolingTaskMap.keySet());
		agents.addAll(deliveryTaskMap.keySet());
		return agents;
	}

	public Map<AID, List<KneadingTask>> getKneadingTaskMap() {
		return kneadingTaskMap;
	}

	public List<KneadingTask> getKneadingTasks() {
		return getTasks(kneadingTaskMap);
	}

	public void addKneadingTask(AID agentId, KneadingTask task) {
		addTask(agentId, task, kneadingTaskMap);
	}

	public Map<AID, List<RestingTask>> getRestingTaskMap() {
		return restingTaskMap;
	}

	public List<RestingTask> getRestingTasks() {
		return getTasks(restingTaskMap);
	}

	public void addRestingTask(AID agentId, RestingTask task) {
		addTask(agentId, task, restingTaskMap);
	}

	public Map<AID, List<ItemPrepTask>> getItemPrepTaskMap() {
		return itemPrepTaskMap;
	}

	public List<ItemPrepTask> getItemPrepTasks() {
		return getTasks(itemPrepTaskMap);
	}

	public void addItemPrepTask(AID agentId, ItemPrepTask task) {
		addTask(agentId, task, itemPrepTaskMap);
	}

	public Map<AID, List<BakingTask>> getBakingTaskMap() {
		return bakingTaskMap;
	}

	public List<BakingTask> getBakingTasks() {
		return getTasks(bakingTaskMap);
	}

	public void addBakingTask(AID agentId, BakingTask task) {
		addTask(agentId, task, bakingTaskMap);
	}

	public Map<AID, List<CoolingTask>> getCoolingTaskMap() {
		return coolingTaskMap;
	}

	public List<CoolingTask> getCoolingTasks() {
		return getTasks(coolingTaskMap);
	}

	public void addCoolingTask(AID agentId, CoolingTask task) {
		addTask(agentId, task, coolingTaskMap);
	}

	public Map<AID, List<DeliveryTask>> getDeliveryTaskMap() {
		return deliveryTaskMap;
	}

	public List<DeliveryTask> getDeliveryTasks() {
		return getTasks(deliveryTaskMap);
	}

	public void addDeliveryTask(AID agentId, DeliveryTask task) {
		addTask(agentId, task, deliveryTaskMap);
	}
	
	public AID getCustomerAgentId() {
		return customerAgentId;
	}

	public void setCustomerAgentId(AID customerAgentId) {
		this.customerAgentId = customerAgentId;
	}

}

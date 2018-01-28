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
	private Map<AID, List<ContractTask<KneadingTask>>> kneadingTaskMap;
	private Map<AID, List<ContractTask<RestingTask>>> restingTaskMap;
	private Map<AID, List<ContractTask<ItemPrepTask>>> itemPrepTaskMap;
	private Map<AID, List<ContractTask<BakingTask>>> bakingTaskMap;
	private Map<AID, List<ContractTask<CoolingTask>>> coolingTaskMap;
	private Map<AID, List<ContractTask<DeliveryTask>>> deliveryTaskMap;
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

	private <T extends Task> void addTask(AID agentId, T task, Map<AID, List<ContractTask<T>>> taskMap) {
		List<ContractTask<T>> existingTasks = taskMap.get(agentId);
		if (existingTasks == null) {
			existingTasks = new LinkedList<>();
		}
		existingTasks.add(new ContractTask<T>(task));
		taskMap.put(agentId, existingTasks);

	}

	private <T extends Task> List<T> getTasks(Map<AID, List<ContractTask<T>>> taskMap) {
		List<T> tasks = new LinkedList<>();
		for (List<ContractTask<T>> existingTasks : taskMap.values()) {
			for (ContractTask<T> contractTask : existingTasks) {
				tasks.add(contractTask.getTask());
			}
		}
		return tasks;
	}

	public <T extends Task> void taskFinished(AID agentId, T task, Map<AID, List<ContractTask<T>>> taskMap) {
		List<ContractTask<T>> contractTasks = taskMap.computeIfAbsent(agentId, k -> new LinkedList<>());
		for (ContractTask<T> contractTask : contractTasks) {
			if (task.equals(contractTask.getTask())) {
				contractTask.setCompleted();
				break;
			}
		}
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

	public Map<AID, List<ContractTask<KneadingTask>>> getKneadingTaskMap() {
		return kneadingTaskMap;
	}

	public Map<AID, List<ContractTask<RestingTask>>> getRestingTaskMap() {
		return restingTaskMap;
	}

	public Map<AID, List<ContractTask<ItemPrepTask>>> getItemPrepTaskMap() {
		return itemPrepTaskMap;
	}

	public Map<AID, List<ContractTask<BakingTask>>> getBakingTaskMap() {
		return bakingTaskMap;
	}

	public Map<AID, List<ContractTask<CoolingTask>>> getCoolingTaskMap() {
		return coolingTaskMap;
	}

	public Map<AID, List<ContractTask<DeliveryTask>>> getDeliveryTaskMap() {
		return deliveryTaskMap;
	}

	public List<KneadingTask> getKneadingTasks() {
		return getTasks(kneadingTaskMap);
	}

	public void addKneadingTask(AID agentId, KneadingTask task) {
		addTask(agentId, task, kneadingTaskMap);
	}

	public List<RestingTask> getRestingTasks() {
		return getTasks(restingTaskMap);
	}

	public void addRestingTask(AID agentId, RestingTask task) {
		addTask(agentId, task, restingTaskMap);
	}

	public List<ItemPrepTask> getItemPrepTasks() {
		return getTasks(itemPrepTaskMap);
	}

	public void addItemPrepTask(AID agentId, ItemPrepTask task) {
		addTask(agentId, task, itemPrepTaskMap);
	}

	public List<BakingTask> getBakingTasks() {
		return getTasks(bakingTaskMap);
	}

	public void addBakingTask(AID agentId, BakingTask task) {
		addTask(agentId, task, bakingTaskMap);
	}

	public List<CoolingTask> getCoolingTasks() {
		return getTasks(coolingTaskMap);
	}

	public void addCoolingTask(AID agentId, CoolingTask task) {
		addTask(agentId, task, coolingTaskMap);
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

	public void kneadingTaskFinished(AID agentId, KneadingTask task) {
		taskFinished(agentId, task, kneadingTaskMap);
	}

	public void restingTaskFinished(AID agentId, RestingTask task) {
		taskFinished(agentId, task, restingTaskMap);
	}

	public void itemPrepTaskFinished(AID agentId, ItemPrepTask task) {
		taskFinished(agentId, task, itemPrepTaskMap);
	}

	public void bakingTaskFinished(AID agentId, BakingTask task) {
		taskFinished(agentId, task, bakingTaskMap);
	}

	public void coolingTaskFinished(AID agentId, CoolingTask task) {
		taskFinished(agentId, task, coolingTaskMap);
	}

	public void deliveryTaskFinished(AID agentId, DeliveryTask task) {
		taskFinished(agentId, task, deliveryTaskMap);
	}

}

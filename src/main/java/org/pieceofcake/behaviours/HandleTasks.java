package org.pieceofcake.behaviours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.Task;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

public abstract class HandleTasks<T extends Task> extends SequentialBehaviour {

	private static final long serialVersionUID = -1122790881400098267L;

	private Date dueDate;
	private List<T> tasks;
	private OrderContract contract;
	private String serviceName;
	private String bakeryName;
	private String protocol;

	public HandleTasks(OrderContract contract, String serviceName, String bakeryName, String protocol) {
		this.contract = contract;
		this.tasks = prepareTasks();
		this.dueDate = getDueDate();
		this.serviceName = serviceName;
		this.bakeryName = bakeryName;
		this.protocol = protocol;
		this.addSubBehaviour(new AdvertiseAndCheckTask());

	}

	public abstract List<T> prepareTasks();
	
	public abstract Date getDueDate();

	public abstract void addTaskToOrder(AID agentId, T task);

	private class AdvertiseAndCheckTask extends SequentialBehaviour {

		private static final long serialVersionUID = 3052021260601546757L;

		private Map<AID, T> bestTaskOffers;

		public AdvertiseAndCheckTask() {
			T task = tasks.remove(0);
			this.bestTaskOffers = new HashMap<>();
			this.addSubBehaviour(
					new AdvertiseTask<>(task, serviceName, bakeryName, protocol, bestTaskOffers));
		}

		@Override
		public int onEnd() {

			boolean offerDatesOk = true;

			for (Task task : bestTaskOffers.values()) {
				if (task.getDueDate().compareTo(dueDate) > 0) {
					offerDatesOk = false;
					break;
				}
			}
			if (!offerDatesOk) {
				contract.setFailed(true);
			} else {
				for (Map.Entry<AID, T> entry : bestTaskOffers.entrySet()) {
					addTaskToOrder(entry.getKey(), entry.getValue());
				}

				if (!tasks.isEmpty()) {
					HandleTasks.this.addSubBehaviour(new AdvertiseAndCheckTask());
				}
			}
			return 0;

		}

	}

}

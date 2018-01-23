package org.pieceofcake.behaviours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pieceofcake.interfaces.TaskDescriptor;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.Task;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

public class HandleTasks<T extends Task> extends SequentialBehaviour {

	private static final long serialVersionUID = -1122790881400098267L;

	private Date dueDate;
	private List<T> tasks;
	private OrderContract contract;
	private TaskDescriptor<T> taskDescriptor;

	public HandleTasks(OrderContract contract, TaskDescriptor<T> taskDescriptor) {
		this.taskDescriptor = taskDescriptor;
		this.contract = contract;
		this.tasks = taskDescriptor.prepareTasks();
		this.dueDate = taskDescriptor.getDueDate();
		this.addSubBehaviour(new AdvertiseAndCheckTask());

	}

	private class AdvertiseAndCheckTask extends SequentialBehaviour {

		private static final long serialVersionUID = 3052021260601546757L;

		private Map<AID, T> bestTaskOffers;

		public AdvertiseAndCheckTask() {
			T task = tasks.remove(0);
			this.bestTaskOffers = new HashMap<>();
			this.addSubBehaviour(new AdvertiseTask<>(task, taskDescriptor.getServiceType(),
					taskDescriptor.getBakeryName(), taskDescriptor.getProtocol(), bestTaskOffers));
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
					taskDescriptor.addTaskToOrder(entry.getKey(), entry.getValue(), contract);
				}

				if (!tasks.isEmpty()) {
					HandleTasks.this.addSubBehaviour(new AdvertiseAndCheckTask());
				}
			}
			return 0;

		}

	}

}

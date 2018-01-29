package org.pieceofcake.behaviours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pieceofcake.interfaces.TaskDescriptor;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.Task;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleTasks<T extends Task> extends SequentialBehaviour {

	private static final long serialVersionUID = -1122790881400098267L;

	private Date dueDate;
	private List<T> tasks;
	private OrderContract contract;
	private TaskDescriptor<T> taskDescriptor;

	public HandleTasks(OrderContract contract, TaskDescriptor<T> taskDescriptor) {
		this.taskDescriptor = taskDescriptor;
		this.contract = contract;
		this.dueDate = taskDescriptor.getDueDate();
		this.addSubBehaviour(new AdvertiseAndCheckTask());

	}

	@Override
	public void onStart() {
		this.tasks = taskDescriptor.prepareTasks();
	}

	private class AdvertiseAndCheckTask extends SequentialBehaviour {

		private static final long serialVersionUID = 3052021260601546757L;

		private Map<AID, T> bestTaskOffers;

		@Override
		public void onStart() {
			if (!tasks.isEmpty() && !contract.hasFailed()) {
				T task = tasks.remove(0);
				this.bestTaskOffers = new HashMap<>();
				this.addSubBehaviour(new AdvertiseTask<>(task, taskDescriptor.getServiceType(),
						taskDescriptor.getBakeryName(), taskDescriptor.getProtocol(), bestTaskOffers));
			}
		}

		@Override
		public int onEnd() {

			boolean offerDatesOk = true;

			if (bestTaskOffers != null && !bestTaskOffers.isEmpty()) {
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
						HandleTasks.this.addSubBehaviour(new AcceptTaskOffer(entry.getKey(), entry.getValue()));
					}

					if (!tasks.isEmpty()) {
						HandleTasks.this.addSubBehaviour(new AdvertiseAndCheckTask());
					}
				}
			}
			return 0;

		}

	}

	private class AcceptTaskOffer extends OneShotBehaviour {

		private static final long serialVersionUID = 4016025396190354291L;

		private AID aid;
		private T task;

		public AcceptTaskOffer(AID aid, T task) {
			this.aid = aid;
			this.task = task;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			msg.addReceiver(aid);
			msg.setProtocol(taskDescriptor.getProtocol());
			msg.setContent(task.toJSONObject().toString());
			myAgent.send(msg);
		}

	}

}

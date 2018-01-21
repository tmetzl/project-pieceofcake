package org.pieceofcake.behaviours;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.*;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class ScheduleOrder extends SequentialBehaviour {

	private static final long serialVersionUID = -7230607737265077273L;

	private OrderContract contract;
	private CookBook cookBook;
	private String bakeryName;

	public ScheduleOrder(OrderContract contract, CookBook cookBook, String bakeryName) {
		this.contract = contract;
		this.cookBook = cookBook;
		this.bakeryName = bakeryName;
		this.addSubBehaviour(new HandleKneadingTasks());

	}

	private class CancelOrderContract extends OneShotBehaviour {

		private static final long serialVersionUID = -5994674106161575658L;

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
			msg.setProtocol(Protocols.CANCEL_ORDER);
			msg.setContent(contract.getOrder().toJSONObject().toString());
			for (AID agent : contract.getAgents()) {
				msg.addReceiver(agent);
			}
			myAgent.send(msg);
			contract = null;
		}

	}

	private class HandleKneadingTasks extends SequentialBehaviour {

		private static final long serialVersionUID = -1122790881400098267L;

		private Date dueDate;
		private List<KneadingTask> kneadingTasks;

		public HandleKneadingTasks() {
			prepareKneadingTasks();
			dueDate = contract.getOrder().getDueDate();
			if (dueDate.compareTo(new Date(dueDate.getDay(), 12, 0, 0)) > 0) {
				dueDate = new Date(dueDate.getDay(), 12, 0, 0);
			}
			this.addSubBehaviour(new AdvertiseAndCheckKneadingTask());

		}

		private void prepareKneadingTasks() {
			kneadingTasks = new LinkedList<>();
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
		}

		private class AdvertiseAndCheckKneadingTask extends SequentialBehaviour {

			private static final long serialVersionUID = 3052021260601546757L;

			private Map<AID, KneadingTask> bestTaskOffers;

			public AdvertiseAndCheckKneadingTask() {

				KneadingTask task = kneadingTasks.remove(0);
				this.bestTaskOffers = new HashMap<>();
				this.addSubBehaviour(new AdvertiseTask<KneadingTask>(task, Services.KNEAD, bakeryName, Protocols.KNEAD,
						bestTaskOffers));

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
					ScheduleOrder.this.addSubBehaviour(new CancelOrderContract());
				} else {
					for (Map.Entry<AID, KneadingTask> entry : bestTaskOffers.entrySet()) {
						contract.addKneadingTask(entry.getKey(), entry.getValue());
					}

					if (!kneadingTasks.isEmpty()) {
						HandleKneadingTasks.this.addSubBehaviour(new AdvertiseAndCheckKneadingTask());
					} else {
						// TODO ScheduleOrder.this.addSubBehaviour(new
						// HandleRestingTasks());
					}

				}
				return 0;

			}

		}

	}

}

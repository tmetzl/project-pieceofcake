package org.pieceofcake.agents;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.pieceofcake.behaviours.CancelOrderContract;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.ScheduleOrder;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.*;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class OrderAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -5029308299914858250L;

	private CookBook cookBook;
	private String bakeryName;
	private List<OrderContract> orderContracts;
	private List<OrderContract> pendingOrderContracts;

	public OrderAgent(Location location, String bakeryName, CookBook cookBook) {
		this.cookBook = cookBook;
		this.bakeryName = bakeryName;
		this.location = location;
		this.orderContracts = new LinkedList<>();
		this.pendingOrderContracts = new LinkedList<>();
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		String welcomeMessage = String.format("Bakery %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		// Register the bakery service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.ORDER);
		sd.setName(Services.ORDER_NAME);
		sd.addProtocols(Protocols.ORDER);
		ServiceDescription infoService = new ServiceDescription();
		infoService.setType(Services.TASK_INFO);
		infoService.setName(bakeryName);
		dfd.addServices(sd);
		dfd.addServices(infoService);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}

		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		addBehaviour(seq);
		addBehaviour(new OrderService());
		addBehaviour(new WaitForStatusUpdate());
	}

	@Override
	protected void takeDown() {
		// Remove from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

	private class OrderService extends CyclicBehaviour {

		private static final long serialVersionUID = 5699193033939119835L;

		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.ORDER);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {

				String jsonOrder = msg.getContent();
				Order order = new Order(new JSONObject(jsonOrder));

				if (msg.getPerformative() == ACLMessage.CFP) {
					// Customer wants an offer

					// Get the price of the order
					Double price = cookBook.getSalesPrice(order);
					ACLMessage reply = msg.createReply();
					if (price != null) {

						addBehaviour(new ScheduleOrderContract(order, msg.getSender()));
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price));
					} else {
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
						myAgent.send(reply);
					}

				} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					// Customer accepted an offer
					Iterator<OrderContract> iter = pendingOrderContracts.iterator();
					while (iter.hasNext()) {
						OrderContract orderContract = iter.next();
						if (order.getGuiId().equals(orderContract.getOrder().getGuiId())) {
							iter.remove();
							orderContracts.add(orderContract);
						}
					}

				} else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
					Iterator<OrderContract> iter = pendingOrderContracts.iterator();
					while (iter.hasNext()) {
						OrderContract orderContract = iter.next();
						if (order.getGuiId().equals(orderContract.getOrder().getGuiId())) {
							iter.remove();
							addBehaviour(new CancelOrderContract(orderContract));
						}
					}
				}
			} else {
				block();
			}
		}

	}

	private class ScheduleOrderContract extends SequentialBehaviour {

		private static final long serialVersionUID = 6003134829282756659L;

		private OrderContract contract;

		public ScheduleOrderContract(Order order, AID customerId) {
			this.contract = new OrderContract(order, customerId);
			this.addSubBehaviour(new ScheduleOrder(contract, cookBook, bakeryName));

		}

		@Override
		public int onEnd() {
			if (contract.hasFailed()) {
				ACLMessage msg = new ACLMessage(ACLMessage.REFUSE);
				msg.addReceiver(contract.getCustomerAgentId());
				msg.setProtocol(Protocols.ORDER);
				msg.setContent("not-completable");
				myAgent.send(msg);
			} else {
				pendingOrderContracts.add(contract);
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.addReceiver(contract.getCustomerAgentId());
				msg.setProtocol(Protocols.ORDER);
				Double price = cookBook.getSalesPrice(contract.getOrder());
				msg.setContent(String.valueOf(price));
				myAgent.send(msg);
			}
			return 0;

		}
	}

	private class WaitForStatusUpdate extends CyclicBehaviour {

		private static final long serialVersionUID = -8401367406818453452L;

		private MessageTemplate template;

		@Override
		public void onStart() {
			MessageTemplate kneadingTemplate = MessageTemplate.MatchProtocol(Protocols.KNEAD);
			MessageTemplate restingTemplate = MessageTemplate.MatchProtocol(Protocols.REST);
			MessageTemplate itemPrepTemplate = MessageTemplate.MatchProtocol(Protocols.PREP);
			MessageTemplate bakingTemplate = MessageTemplate.MatchProtocol(Protocols.BAKE);
			MessageTemplate coolingTemplate = MessageTemplate.MatchProtocol(Protocols.COOL);
			MessageTemplate deliveryTemplate = MessageTemplate.MatchProtocol(Protocols.DELIVERY);
			template = MessageTemplate.or(kneadingTemplate, restingTemplate);
			template = MessageTemplate.or(template, itemPrepTemplate);
			template = MessageTemplate.or(template, bakingTemplate);
			template = MessageTemplate.or(template, coolingTemplate);
			template = MessageTemplate.or(template, deliveryTemplate);
			template = MessageTemplate.and(template, MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		}

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(template);
			if (msg != null) {
				OrderContract contract;
				switch (msg.getProtocol()) {

				case Protocols.KNEAD:
					KneadingTask kneadingTask = new KneadingTask();
					kneadingTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(kneadingTask.getOrderId());
					if (contract != null) {
						contract.kneadingTaskFinished(msg.getSender(), kneadingTask);
					}
					break;
				case Protocols.REST:
					RestingTask restingTask = new RestingTask();
					restingTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(restingTask.getOrderId());
					if (contract != null) {
						contract.restingTaskFinished(msg.getSender(), restingTask);
					}
					break;
				case Protocols.PREP:
					ItemPrepTask itemPrepTask = new ItemPrepTask();
					itemPrepTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(itemPrepTask.getOrderId());
					if (contract != null) {
						contract.itemPrepTaskFinished(msg.getSender(), itemPrepTask);
					}
					break;
				case Protocols.BAKE:
					BakingTask bakingTask = new BakingTask();
					bakingTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(bakingTask.getOrderId());
					if (contract != null) {
						contract.bakingTaskFinished(msg.getSender(), bakingTask);
					}
					break;
				case Protocols.COOL:
					CoolingTask coolingTask = new CoolingTask();
					coolingTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(coolingTask.getOrderId());
					if (contract != null) {
						contract.coolingTaskFinished(msg.getSender(), coolingTask);
					}
					break;
				case Protocols.DELIVERY:
					DeliveryTask deliveryTask = new DeliveryTask();
					deliveryTask.fromJSONObject(new JSONObject(msg.getContent()));
					contract = getOrderContract(deliveryTask.getOrderId());
					if (contract != null) {
						contract.deliveryTaskFinished(msg.getSender(), deliveryTask);
					}
					break;
				default:
					break;
				}
			} else {
				block();
			}

		}

		private OrderContract getOrderContract(String orderId) {
			for (OrderContract orderContract : orderContracts) {
				if (orderContract.getOrder().getGuiId().equals(orderId)) {
					return orderContract;
				}
			}
			return null;
		}

	}
}

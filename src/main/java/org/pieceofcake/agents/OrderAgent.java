package org.pieceofcake.agents;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.pieceofcake.behaviours.CancelOrderContract;
import org.pieceofcake.behaviours.NotifyOrderComplete;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.ScheduleOrder;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.*;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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

	public OrderAgent(Location location, String bakeryName, CookBook cookBook) {
		this.cookBook = cookBook;
		this.bakeryName = bakeryName;
		this.location = location;
		this.orderContracts = new LinkedList<>();
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
		addBehaviour(new OrderService2());
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

	private class OrderService2 extends SequentialBehaviour {

		private static final long serialVersionUID = -7336513100493959374L;

		private OrderContract contract;

		public OrderService2() {
			this.addSubBehaviour(new WaitForOrder());
		}

		@Override
		public int onEnd() {
			myAgent.addBehaviour(new OrderService2());
			return 0;
		}

		private class WaitForOrder extends Behaviour {

			private static final long serialVersionUID = -860949815687599667L;

			private boolean orderReceived;

			public WaitForOrder() {
				this.orderReceived = false;
			}

			@Override
			public void action() {
				MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol(Protocols.ORDER),
						MessageTemplate.MatchPerformative(ACLMessage.CFP));
				ACLMessage msg = myAgent.receive(msgTemplate);
				if (msg != null) {
					String jsonOrder = msg.getContent();
					Order order = new Order(new JSONObject(jsonOrder));
					Date orderDate = new Date(getScenarioClock().getDate().toSeconds() + 3600l);
					Date dueDate = new Date(order.getDueDate().toSeconds() - 3600);
					order.setOrderDate(orderDate);
					order.setDueDate(dueDate);
					contract = new OrderContract(order, msg.getSender());
					orderReceived = true;
					OrderService2.this.addSubBehaviour(new CheckInStock());
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return orderReceived;
			}

		}

		private class CheckInStock extends OneShotBehaviour {

			private static final long serialVersionUID = 8949706952997896400L;

			@Override
			public void action() {
				Order order = contract.getOrder();
				Double price = cookBook.getSalesPrice(order);
				if (price != null) {
					orderContracts.add(contract);
					OrderService2.this.addSubBehaviour(new ScheduleOrder(contract, cookBook, bakeryName));
					OrderService2.this.addSubBehaviour(new CheckSchedule());
				} else {
					OrderService2.this.addSubBehaviour(new RefuseOrder());
				}
			}

		}

		private class CheckSchedule extends OneShotBehaviour {

			private static final long serialVersionUID = -8179200632013016315L;

			@Override
			public void action() {
				if (contract.hasFailed()) {
					Iterator<OrderContract> iter = orderContracts.iterator();
					while (iter.hasNext()) {
						OrderContract orderContract = iter.next();
						if (contract.getOrder().getGuiId().equals(orderContract.getOrder().getGuiId())) {
							iter.remove();
							addBehaviour(new CancelOrderContract(orderContract));
						}
					}
					OrderService2.this.addSubBehaviour(new RefuseOrder());
				} else {
					OrderService2.this.addSubBehaviour(new SendProposol());
				}
			}

		}

		private class RefuseOrder extends OneShotBehaviour {

			private static final long serialVersionUID = 2465736864860484614L;

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.REFUSE);
				msg.addReceiver(contract.getCustomerAgentId());
				msg.setProtocol(Protocols.ORDER);
				msg.setContent("not-available");
				myAgent.send(msg);
			}

		}

		private class SendProposol extends OneShotBehaviour {

			private static final long serialVersionUID = 7960339631635199169L;

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
				msg.addReceiver(contract.getCustomerAgentId());
				msg.setProtocol(Protocols.ORDER);
				Double price = cookBook.getSalesPrice(contract.getOrder());
				msg.setContent(String.valueOf(price));
				myAgent.send(msg);
				addBehaviour(new WaitForAnswer(contract));
			}

		}

		

	}
	
	private class WaitForAnswer extends Behaviour {

		private static final long serialVersionUID = -8475806512645064348L;

		private boolean answerReceived;
		private OrderContract contract;
		
		public WaitForAnswer(OrderContract contract) {
			this.contract = contract;
			this.answerReceived = false;
		}

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol(Protocols.ORDER),
					MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL),
							MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)));
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					orderContracts.add(contract);
				} else {
					Iterator<OrderContract> iter = orderContracts.iterator();
					while (iter.hasNext()) {
						OrderContract orderContract = iter.next();
						if (contract.getOrder().getGuiId().equals(orderContract.getOrder().getGuiId())) {
							iter.remove();
							addBehaviour(new CancelOrderContract(orderContract));
						}
					}
				}
				answerReceived = true;
			} else {
				block();
			}

		}

		@Override
		public boolean done() {
			return answerReceived;
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
				switch (msg.getProtocol()) {
				case Protocols.KNEAD:
					handleKneadingStatusMessage(msg);
					break;
				case Protocols.REST:
					handleRestingStatusMessage(msg);
					break;
				case Protocols.PREP:
					handleItemPrepStatusMessage(msg);
					break;
				case Protocols.BAKE:
					handleBakingStatusMessage(msg);
					break;
				case Protocols.COOL:
					handleCoolingStatusMessage(msg);
					break;
				case Protocols.DELIVERY:
					handleDeliveryStatusMessage(msg);
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

		private void handleKneadingStatusMessage(ACLMessage msg) {
			KneadingTask kneadingTask = new KneadingTask();
			kneadingTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(kneadingTask.getOrderId());
			if (contract != null) {
				contract.kneadingTaskFinished(msg.getSender(), kneadingTask);
			}
		}

		private void handleRestingStatusMessage(ACLMessage msg) {
			RestingTask restingTask = new RestingTask();
			restingTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(restingTask.getOrderId());
			if (contract != null) {
				contract.restingTaskFinished(msg.getSender(), restingTask);
			}
		}

		private void handleItemPrepStatusMessage(ACLMessage msg) {
			ItemPrepTask itemPrepTask = new ItemPrepTask();
			itemPrepTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(itemPrepTask.getOrderId());
			if (contract != null) {
				contract.itemPrepTaskFinished(msg.getSender(), itemPrepTask);
			}
		}

		private void handleBakingStatusMessage(ACLMessage msg) {
			BakingTask bakingTask = new BakingTask();
			bakingTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(bakingTask.getOrderId());
			if (contract != null) {
				contract.bakingTaskFinished(msg.getSender(), bakingTask);
			}
		}

		private void handleCoolingStatusMessage(ACLMessage msg) {
			CoolingTask coolingTask = new CoolingTask();
			coolingTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(coolingTask.getOrderId());
			if (contract != null) {
				contract.coolingTaskFinished(msg.getSender(), coolingTask);
			}
		}

		private void handleDeliveryStatusMessage(ACLMessage msg) {
			DeliveryTask deliveryTask = new DeliveryTask();
			deliveryTask.fromJSONObject(new JSONObject(msg.getContent()));
			OrderContract contract = getOrderContract(deliveryTask.getOrderId());
			if (contract != null) {
				contract.deliveryTaskFinished(msg.getSender(), deliveryTask);
				if (contract.isCompleted()) {
					addBehaviour(new NotifyOrderComplete(contract));
				}
			}
		}

	}

}

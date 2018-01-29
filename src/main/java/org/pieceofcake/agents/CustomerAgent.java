package org.pieceofcake.agents;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.pieceofcake.behaviours.DelayUntilDate;
import org.pieceofcake.behaviours.FindAgents;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.CustomerContract;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.utils.OrderDateComparator;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class CustomerAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -7440684252102998234L;

	private String guiId;
	private int type;
	private List<Order> orders;
	private List<Order> placedOrders;
	private List<Order> failedOrders;
	private List<CustomerContract> contracts;

	public CustomerAgent(String guiId, int type, Location location, List<Order> orders) {
		this.guiId = guiId;
		this.type = type;
		this.location = location;

		Collections.sort(orders, new OrderDateComparator());
		this.orders = orders;
		this.placedOrders = new LinkedList<>();
		this.failedOrders = new LinkedList<>();
		this.contracts = new LinkedList<>();
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		String welcomeMessage = String.format("Customer %s of type %d at location (%.2f,%.2f) is ready!",
				getAID().getLocalName(), type, getLocation().getX(), getLocation().getY());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		seq.addSubBehaviour(new PlaceOrder(orders.get(0)));

		addBehaviour(seq);
		addBehaviour(new WaitForOrderComplete());

	}

	private class PlaceOrder extends SequentialBehaviour {

		private static final long serialVersionUID = -7046069112173445913L;

		private int numOfReplies = 0;
		private double bestPrice = 0;
		private AID bestSeller;
		private List<AID> bakeries;
		private List<AID> proposers;
		private Order order;

		public PlaceOrder(Order order) {
			this.order = order;
			this.bakeries = new LinkedList<>();
			this.proposers = new LinkedList<>();
			this.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
			this.addSubBehaviour(new DelayUntilDate(getScenarioClock(), order.getOrderDate()));
			this.addSubBehaviour(new FindAgents(Services.ORDER, Services.ORDER_NAME, bakeries));
			this.addSubBehaviour(new RequestOffers());
			this.addSubBehaviour(new ReceiveOffers());
			this.addSubBehaviour(new RejectProposals());
			this.addSubBehaviour(new OrderFromBestSeller());
		}

		@Override
		public int onEnd() {
			orders.remove(0);
			if (!orders.isEmpty()) {
				myAgent.addBehaviour(new PlaceOrder(orders.get(0)));
			}
			return 0;
		}

		private class RequestOffers extends OneShotBehaviour {

			private static final long serialVersionUID = 1828563460495980614L;

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				// Add all known bakeries as receivers
				for (AID bakery : bakeries) {
					msg.addReceiver(bakery);
				}
				msg.setLanguage("English");
				msg.setOntology("Bakery-order-ontology");
				msg.setProtocol(Protocols.ORDER);
				msg.setReplyWith("offer-request-" + System.currentTimeMillis());
				String content = order.toJSONObject().toString();
				msg.setContent(content);
				myAgent.send(msg);
			}

		}

		private class ReceiveOffers extends Behaviour {

			private static final long serialVersionUID = 1435087248061140988L;

			private boolean allOffersReceived = false;

			@Override
			public void action() {
				MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.ORDER);
				ACLMessage offer = myAgent.receive(msgTemplate);
				if (offer != null) {

					String answerContent = offer.getContent();

					if (offer.getPerformative() == ACLMessage.PROPOSE) {
						proposers.add(offer.getSender());
						double price = Double.parseDouble(answerContent);

						if (bestSeller == null || price < bestPrice) {
							bestPrice = price;
							bestSeller = offer.getSender();
						}
					}
					numOfReplies++;
					if (numOfReplies >= bakeries.size()) {
						// All replies received, terminate behavior
						allOffersReceived = true;
					}
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return allOffersReceived;
			}

		}

		private class RejectProposals extends OneShotBehaviour {

			private static final long serialVersionUID = -1149541077346212172L;

			@Override
			public void action() {
				Iterator<AID> iter = proposers.iterator();

				while (iter.hasNext()) {
					AID bakery = iter.next();
					if (bakery.getName().equals(bestSeller.getName())) {
						iter.remove();
						break;
					}
				}
				if (!proposers.isEmpty()) {
					ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
					msg.setProtocol(Protocols.ORDER);
					msg.setContent(order.toJSONObject().toString());
					for (AID proposer : proposers) {
						msg.addReceiver(proposer);
					}
					myAgent.send(msg);
				}

			}

		}

		private class OrderFromBestSeller extends OneShotBehaviour {

			private static final long serialVersionUID = 4933205434400567122L;

			@Override
			public void action() {
				if (bestSeller != null) {
					String output = String.format("%s: The best offer for order %s of EUR %.2f comes from %s.",
							getAID().getLocalName(), order.getGuiId(), bestPrice, bestSeller.getLocalName());
					logger.log(Logger.INFO, output);

					ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					// Add all known bakeries as receivers
					msg.addReceiver(bestSeller);
					msg.setLanguage("English");
					msg.setOntology("Bakery-order-ontology");
					msg.setProtocol(Protocols.ORDER);
					msg.setReplyWith("offer-confirm-" + System.currentTimeMillis());
					String content = order.toJSONObject().toString();
					msg.setContent(content);
					myAgent.send(msg);
					placedOrders.add(order);
					contracts.add(new CustomerContract(order, bestSeller));

				} else {
					String output = String.format("%s: Order %s, no offers received or products not available",
							myAgent.getLocalName(), order.getGuiId());
					logger.log(Logger.INFO, output);
					failedOrders.add(order);
				}
			}

		}

	}

	private class WaitForOrderComplete extends CyclicBehaviour {

		private static final long serialVersionUID = -5351685996831509437L;

		@Override
		public void action() {
			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(Protocols.ORDER_COMPLETE),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(template);
			if (msg != null) {
				Order order = new Order(new JSONObject(msg.getContent()));
				for (CustomerContract contract : contracts) {
					if (order.getGuiId().equals(contract.getOrder().getGuiId())) {
						contract.setCompleted();
						String output = String.format("%s completed at %s, was due at %s",
								contract.getOrder().getGuiId(), getScenarioClock().getDate(),
								contract.getOrder().getDueDate());
						logger.log(Logger.INFO, output);
					}
				}
			}

		}

	}

	public String getGuiId() {
		return guiId;
	}

	public int getType() {
		return type;
	}

	public List<Order> getOrders() {
		return orders;
	}

}

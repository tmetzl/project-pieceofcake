package maas.agents;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import maas.config.Protocols;
import maas.objects.Order;
import maas.utils.OrderDateComparator;

public class CustomerAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -7440684252102998234L;

	private String guiId;
	private int type;
	private double locationX;
	private double locationY;
	private List<Order> orders;
	private List<Order> placedOrders;
	private List<Order> failedOrders;

	public CustomerAgent(String guiId, int type, double locationX, double locationY, List<Order> orders) {
		this.guiId = guiId;
		this.type = type;
		this.locationX = locationX;
		this.locationY = locationY;

		Collections.sort(orders, new OrderDateComparator());
		this.orders = orders;
		this.placedOrders = new LinkedList<>();
		this.failedOrders = new LinkedList<>();

	}

	@Override
	protected void setup() {
		super.setup();

		// Printout a welcome message
		String welcomeMessage = String.format("Customer %s of type %d at location (%.2f,%.2f) is ready!",
				getAID().getLocalName(), type, locationX, locationY);
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock());
		seq.addSubBehaviour(new WaitForStart());
		seq.addSubBehaviour(new PlaceOrder(orders.get(0)));

		addBehaviour(seq);

	}

	private class PlaceOrder extends SequentialBehaviour {

		private static final long serialVersionUID = -7046069112173445913L;

		private int numOfReplies = 0;
		private double bestPrice = 0;
		private AID bestSeller;
		private AID[] bakeries;
		private Order order;

		public PlaceOrder(Order order) {
			this.order = order;
			this.addSubBehaviour(new SynchronizeClock());
			this.addSubBehaviour(new DelayUntilNextOrder());
			this.addSubBehaviour(new UpdateBakeries());
			this.addSubBehaviour(new RequestOffers());
			this.addSubBehaviour(new ReceiveOffers());
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

		private class DelayUntilNextOrder extends Behaviour {

			private static final long serialVersionUID = -2913887133905014293L;

			private boolean waitingFinished = false;

			@Override
			public void action() {
				long currentTime = getScenarioTime();
				long remainingTime = order.getOrderDate() - currentTime;

				if (currentTime >= order.getOrderDate()) {
					waitingFinished = true;
				} else {
					block(remainingTime * 1000l);
				}

			}

			@Override
			public boolean done() {
				return waitingFinished;
			}

		}

		private class UpdateBakeries extends OneShotBehaviour {

			private static final long serialVersionUID = 8365966822569563888L;

			@Override
			public void action() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("bakery");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					bakeries = new AID[result.length];
					for (int i = 0; i < result.length; i++) {
						bakeries[i] = result[i].getName();
					}
				} catch (FIPAException fe) {
					logger.log(Logger.WARNING, fe.getMessage(), fe);
				}
			}

		}

		private class RequestOffers extends OneShotBehaviour {

			private static final long serialVersionUID = 1828563460495980614L;

			@Override
			public void action() {
				long time = getScenarioTime();

				String output = String.format("%nDay %d Hour %d%n%s", time / 24, time % 24, order);
				logger.log(Logger.INFO, output);

				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				// Add all known bakeries as receivers
				for (int i = 0; i < bakeries.length; i++) {
					msg.addReceiver(bakeries[i]);
				}
				msg.setLanguage("English");
				msg.setOntology("Bakery-order-ontology");
				msg.setProtocol(Protocols.ORDER);
				msg.setReplyWith("offer-request-" + System.currentTimeMillis());
				String content = order.toJSONString();
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
						double price = Double.parseDouble(answerContent);

						if (bestSeller == null || price < bestPrice) {
							bestPrice = price;
							bestSeller = offer.getSender();
						}
					}
					numOfReplies++;
					if (numOfReplies >= bakeries.length) {
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

		private class OrderFromBestSeller extends OneShotBehaviour {

			private static final long serialVersionUID = 4933205434400567122L;

			@Override
			public void action() {
				if (bestSeller != null) {
					String output = String.format("%s: The best offer of EUR %.2f comes from %s.",
							getAID().getLocalName(), bestPrice, bestSeller.getLocalName());
					logger.log(Logger.INFO, output);

					ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					// Add all known bakeries as receivers
					msg.addReceiver(bestSeller);
					msg.setLanguage("English");
					msg.setOntology("Bakery-order-ontology");
					msg.setProtocol(Protocols.ORDER);
					msg.setReplyWith("offer-confirm-" + System.currentTimeMillis());
					String content = order.toJSONString();
					msg.setContent(content);
					myAgent.send(msg);
					placedOrders.add(order);

				} else {
					logger.log(Logger.INFO, myAgent.getLocalName() + ": No offers received or products not available.");
					failedOrders.add(order);
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

	public double getLocationX() {
		return locationX;
	}

	public double getLocationY() {
		return locationY;
	}

	public List<Order> getOrders() {
		return orders;
	}

}

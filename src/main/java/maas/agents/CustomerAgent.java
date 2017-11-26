package maas.agents;

import java.util.Collections;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.objects.Order;
import maas.utils.OrderDateComparator;

@SuppressWarnings("serial")
public class CustomerAgent extends Agent {

	private String guiId;
	private int type;
	private int locationX;
	private int locationY;
	private transient List<Order> orders;
	private Logger logger;

	public CustomerAgent(String guiId, int type, int locationX, int locationY, List<Order> orders) {
		this.guiId = guiId;
		this.type = type;
		this.locationX = locationX;
		this.locationY = locationY;

		Collections.sort(orders, new OrderDateComparator());
		this.orders = orders;

	}

	@Override
	protected void setup() {
		// Create our logger
		logger = Logger.getJADELogger(this.getClass().getName());

		// Printout a welcome message
		System.out.println("Created the customer " + getAID().getLocalName() + " of type " + this.type
				+ " at location (" + this.locationX + ", " + this.locationY + ")");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
		addBehaviour(new PlaceOrder());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class PlaceOrder extends SequentialBehaviour {

		private int numOfReplies = 0;
		private int bestPrice = 0;
		private AID bestSeller;
		private AID[] bakeries;

		public PlaceOrder() {
			this.addSubBehaviour(new UpdateBakeries());
			this.addSubBehaviour(new RequestOffers());
			this.addSubBehaviour(new ReceiveOffers());
			this.addSubBehaviour(new OrderFromBestSeller());
		}

		private class UpdateBakeries extends OneShotBehaviour {

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

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				// Add all known bakeries as receivers
				for (int i = 0; i < bakeries.length; i++) {
					msg.addReceiver(bakeries[i]);
				}
				msg.setLanguage("English");
				msg.setOntology("Bakery-order-ontology");
				msg.setReplyWith("offer-request-" + System.currentTimeMillis());
				String content = orders.get(0).toJSONString();
				msg.setContent(content);
				myAgent.send(msg);
			}

		}

		private class ReceiveOffers extends Behaviour {

			private boolean allOffersReceived = false;

			@Override
			public void action() {
				ACLMessage answer = myAgent.receive();
				if (answer != null) {

					String answerContent = answer.getContent();

					if (answer.getPerformative() == ACLMessage.PROPOSE) {
						System.out.println("Price: " + answerContent);
						int price = Integer.parseInt(answerContent);

						if (bestSeller == null || price < bestPrice) {
							bestPrice = price;
							bestSeller = answer.getSender();
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

			@Override
			public void action() {
				if (bestSeller != null) {
					System.out.println(myAgent.getLocalName() + ": The best offer of EUR " + bestPrice + " comes from "
							+ bestSeller.getLocalName() + ".");
					ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					// Add all known bakeries as receivers
					msg.addReceiver(bestSeller);
					msg.setLanguage("English");
					msg.setOntology("Bakery-order-ontology");
					msg.setReplyWith("offer-confirm-" + System.currentTimeMillis());
					String content = orders.get(0).toJSONString();
					msg.setContent(content);
					myAgent.send(msg);
				} else {
					System.out.println(myAgent.getLocalName() + ": No offers received or products not available.");
				}
			}

		}
	}

}

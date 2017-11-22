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
	private List<Order> orders;
	private AID[] bakeries;
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
		addBehaviour(new PlaceOrder(orders));
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	// Behavior for placing orders
	private class PlaceOrder extends Behaviour {

		// Indicates at what step of the process we are
		private int step = 0;
		private int numOfReplies = 0;
		private int bestPrice = 0;
		private AID bestSeller;
		private List<Order> orders;

		public PlaceOrder(List<Order> orders) {
			this.orders = orders;
		}

		public void updateBakeries() {
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

		public void action() {
			if (step == 0) {
				// Step 0, place the order
				// First update the list of bakeries
				updateBakeries();

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

				System.out.println("order successfully placed");
				// Go to the next step
				step = 1;

			} else if (step == 1) {
				// Step 1, wait for replies
				ACLMessage answer = myAgent.receive();
				if (answer != null) {

					// System.out.println("Checking for msg");
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
						System.out.println("All replies received.");
						step = 2;
					}
				} else {
					block();
				}
			} else if (step == 2) {
				if (bestSeller != null) {
					System.out.println(
							"The best offer of EUR " + bestPrice + " comes from " + bestSeller.getLocalName() + ".");
				} else {
					System.out.println("No offers received or products not available.");
				}
				step = 3;
			}
		}

		public boolean done() {
			return step == 3;
		}
	}
}

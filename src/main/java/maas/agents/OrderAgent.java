package maas.agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.objects.Bakery;
import maas.objects.Order;

@SuppressWarnings("serial")
public class OrderAgent extends Agent {

	private List<Order> orders = new LinkedList<Order>();
	private Bakery myBakery;
	private Logger logger;

	public OrderAgent(Bakery bakery) {
		this.myBakery = bakery;
	}

	@Override
	protected void setup() {
		// Create our logger
		logger = Logger.getJADELogger(this.getClass().getName());
		// Printout a welcome message
		System.out.println("Hello! Baker-agent " + getAID().getName() + " is ready.");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Logger logger = Logger.getJADELogger(this.getClass().getName());
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
		// Register the bakery service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery");
		sd.setName("Bakery-ordering");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		addBehaviour(new OrderService());
	}

	@Override
	protected void takeDown() {
		// Remove from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	// Cyclic order receiving behavior
	private class OrderService extends CyclicBehaviour {
		public void action() {

			ACLMessage msg = myAgent.receive();
			if (msg != null) {

				if (msg.getPerformative() == ACLMessage.CFP) {
					// Customer wants an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(jsonOrder);

					// Get the price of the order
					Integer price = myBakery.getPrice(order);
					ACLMessage reply = msg.createReply();
					if (price != null) {
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price));
					} else {
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
					}
					// orders.add(order);
					// System.out.println("So you want " + order);

					myAgent.send(reply);
				} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					// Customer accepted an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(jsonOrder);
					// Add to active orders
					orders.add(order);
					System.out.println(myAgent.getLocalName() + ": Customer " + msg.getSender().getLocalName() + " ordered:\n" + order + ".");
				}
			} else {
				block();

			}

		}
	}
}

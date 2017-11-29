package maas.agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import maas.config.Protocols;
import maas.objects.Bakery;
import maas.objects.Order;

@SuppressWarnings("serial")
public class OrderAgent extends SynchronizedAgent {

	private transient List<Order> orders = new LinkedList<>();
	private transient Bakery myBakery;

	public OrderAgent(Bakery bakery) {
		this.myBakery = bakery;
	}

	@Override
	protected void setup() {
		super.setup();
		// Printout a welcome message
		System.out.println("Hello! Baker-agent " + getAID().getName() + " is ready.");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
		
		// Register the bakery service in the yellow pages
		ServiceDescription sd = new ServiceDescription();
		sd.setType("bakery");
		sd.setName("Bakery-ordering");		
		registerService(sd);
		
		addBehaviour(new SynchronizeClock());
		addBehaviour(new WaitForStart());
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
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.ORDER);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.CFP) {
					// Customer wants an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(jsonOrder);

					// Get the price of the order
					Double price = myBakery.getPrice(order);
					ACLMessage reply = msg.createReply();
					if (price != null) {
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price));
					} else {
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
					}

					myAgent.send(reply);
				} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					// Customer accepted an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(jsonOrder);
					// Add to active orders
					orders.add(order);
				}
			} else {
				block();

			}

		}
	}
}

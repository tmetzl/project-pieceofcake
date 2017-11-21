package maas.agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.objects.Bakery;
import maas.objects.Order;

@SuppressWarnings("serial")
public class OrderAgent extends Agent {
	
	private List<Order> orders = new LinkedList<Order>();
	private Bakery myBakery;
	
	public OrderAgent(Bakery bakery) {
		this.myBakery = bakery;
	}
	
	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Baker-agent " + getAID().getName() + " is ready.");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Logger logger = Logger.getJADELogger(this.getClass().getName());
			logger.log(Logger.WARNING, e.getMessage(), e);	
			Thread.currentThread().interrupt();
		}
		addBehaviour(new OrderService());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	// cyclic order receiving behavior
	private class OrderService extends CyclicBehaviour {
		public void action() {

			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String jsonOrder = msg.getContent();
				Order order = new Order(jsonOrder);
				
				// Get the price of the order
				int price = myBakery.getPrice(order);
				orders.add(order);
				System.out.println("So you want " + order);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent("Give me EUR " + price);
				myAgent.send(reply);
			} else {
				block();

			}

		}
	}
}

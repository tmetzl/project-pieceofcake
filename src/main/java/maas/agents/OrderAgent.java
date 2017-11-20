package maas.agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import maas.objects.Order;

@SuppressWarnings("serial")
public class OrderAgent extends Agent {
	
	private List<Order> orders = new LinkedList<Order>();
	
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Baker-agent " + getAID().getName() + " is ready.");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
		addBehaviour(new OrderService());
		//addBehaviour(new shutdown());

	}

	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	// cyclic order receiving behavior
	private class OrderService extends CyclicBehaviour {
		public void action() {

			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String order = msg.getContent();
				Order orderObject = new Order(order);
				orders.add(orderObject);
				System.out.println("So you want " + orderObject);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent("5 euros");
				myAgent.send(reply);
			} else {
				block();

			}

		}
	}
}

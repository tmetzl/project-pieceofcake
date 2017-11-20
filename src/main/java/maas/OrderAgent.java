package maas;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class OrderAgent extends Agent {
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
				System.out.println("So you want " + order);
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

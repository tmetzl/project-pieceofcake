package maas.agents;

import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.objects.Bakery;
import maas.objects.Order;
import maas.objects.Product;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends Agent {

	private AID[] kneadingAgents;
	private List<String> listOfDough;
	private Bakery myBakery;
	boolean[] kneadingMachineFree;

	public KneadingSchedulerAgent(String[] kneadingAgentNames, List<String> listOfDough, Bakery myBakery) {
		this.myBakery = myBakery;
		this.kneadingAgents = new AID[kneadingAgentNames.length];
		for (int i = 0; i < kneadingAgentNames.length; i++) {
			this.kneadingAgents[i] = new AID(kneadingAgentNames[i], AID.ISLOCALNAME);
		}
		this.kneadingMachineFree = new boolean[kneadingAgents.length];
		Arrays.fill(kneadingMachineFree, true);
		this.listOfDough = listOfDough;

	}

	public String getJSONMessage(Bakery myBakery, String dough) {
		Product product = myBakery.getProductByName(dough);
		long kneadingTime = product.getDoughPrepTime();
		String jsonMessage = String.format("{\"%s\":%d}", dough, kneadingTime);
		return jsonMessage;
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! KneadingSchedulerAgent " + getAID().getName() + " is ready.");
		addBehaviour(new RequestKneading());
		addBehaviour(new ReceiveKneadedDough());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class RequestKneading extends SequentialBehaviour {

		private String request;
		int myFreeMachine;

		public RequestKneading() {

			this.addSubBehaviour(new FindNextFreeKneadingMachine());
			this.addSubBehaviour(new SendDoughToKnead());

		}

		private class FindNextFreeKneadingMachine extends Behaviour {

			boolean freeMachineFound = false;

			@Override
			public void action() {

				for (int i = 0; i < kneadingMachineFree.length; i++) {
					boolean isFree = kneadingMachineFree[i];
					if (isFree) {
						myFreeMachine = i;
						freeMachineFound = true;
						break;
					}
				}
				if (!freeMachineFound) {
					block(500);
				}

			}

			@Override
			public boolean done() {
				return freeMachineFound;
			}

		}

		private class SendDoughToKnead extends OneShotBehaviour {

			@Override
			public void action() {
				ACLMessage kneadingRequest = new ACLMessage(ACLMessage.REQUEST);
				kneadingRequest.addReceiver(kneadingAgents[myFreeMachine]);
				request = getJSONMessage(myBakery, listOfDough.get(0));
				kneadingRequest.setContent(request);
				kneadingRequest.setLanguage("English");
				kneadingRequest.setOntology("Bakery-order-ontology");
				myAgent.send(kneadingRequest);
				kneadingMachineFree[myFreeMachine] = false;
				System.out.println("Set machine " + myFreeMachine + " to occupied.");
				listOfDough.remove(0);

			}

		}

	}

	private class ReceiveKneadedDough extends CyclicBehaviour {

		private String response;
		private AID kneadingAgentId;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				response = msg.getContent();
				kneadingAgentId = msg.getSender();
				// setting the agent to be free
				for (int i = 0; i < kneadingAgents.length; i++) {
					if (kneadingAgents[i].equals(kneadingAgentId)) {
						kneadingMachineFree[i] = true;
						System.out.println("Set machine " + i + " to free.");
						break;
					}
				}

			} else {
				block();
			}

		}

	}
}

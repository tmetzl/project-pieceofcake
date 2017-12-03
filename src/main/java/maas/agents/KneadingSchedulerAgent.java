package maas.agents;

import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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
	private String[] listOfDough;
	private Bakery myBakery;
	boolean[] kneadingMachineFree;
	int myFreeMachine;

	public KneadingSchedulerAgent(AID[] kneadingAgents) {
		this.kneadingAgents = kneadingAgents;
		this.kneadingMachineFree = new boolean[kneadingAgents.length];
		Arrays.fill(kneadingMachineFree, true);

	}

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! KneadingSchedulerAgen " + getAID().getName() + " is ready.");
		// addBehaviour(new ProcessKneadingRequest());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class RequestKneading extends SequentialBehaviour {
		
		private String request;
		
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
				kneadingRequest.setContent(request);
				kneadingRequest.setLanguage("English");
				kneadingRequest.setOntology("Bakery-order-ontology");
				myAgent.send(kneadingRequest);

			}
			
		}

	}
}

package maas.agents;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import maas.config.Protocols;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends Agent {

	private AID[] kneadingAgents;
	private boolean[] kneadingMachineFree;
	private Queue<KneadingInfo> doughQueue = new LinkedList<>();
	private AID DoughFactoryAgentId;
	private boolean requestKneadingRunning = false;

	public KneadingSchedulerAgent(String[] kneadingAgentNames) {
		this.kneadingAgents = new AID[kneadingAgentNames.length];
		for (int i = 0; i < kneadingAgentNames.length; i++) {
			this.kneadingAgents[i] = new AID(kneadingAgentNames[i], AID.ISLOCALNAME);
		}
		this.kneadingMachineFree = new boolean[kneadingAgents.length];
		Arrays.fill(kneadingMachineFree, true);

	}

	public String getJSONMessage(KneadingInfo dough) {
		String product = dough.productName;
		long kneadingTime = dough.kneadingTime;
		String jsonMessage = String.format("{\"%s\":%d}", product, kneadingTime);
		return jsonMessage;
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! KneadingSchedulerAgent " + getAID().getName() + " is ready.");
		addBehaviour(new ReceiveDoughMessage());
		// addBehaviour(new RequestKneading());
		addBehaviour(new ReceiveKneadedDough());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class KneadingInfo {

		private String productName;
		private long kneadingTime;
	}

	private class ReceiveDoughMessage extends CyclicBehaviour {

		private String doughRequest;

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.DOUGH);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				doughRequest = msg.getContent();
				DoughFactoryAgentId = msg.getSender();
				JSONObject obj = new JSONObject(doughRequest);
				String[] names = JSONObject.getNames(obj);

				for (int i = 0; i < names.length; i++) {
					KneadingInfo dough = new KneadingInfo();
					dough.productName = names[i];
					dough.kneadingTime = obj.getLong(names[i]);
					doughQueue.add(dough);
				}
				if (!requestKneadingRunning) {
					myAgent.addBehaviour(new RequestKneading());
					requestKneadingRunning = true;
				}

			} else {
				block();
			}

		}

	}

	private class RequestKneading extends SequentialBehaviour {

		private String request;
		int myFreeMachine;

		public RequestKneading() {

			this.addSubBehaviour(new FindNextFreeKneadingMachine());
			this.addSubBehaviour(new SendDoughToKnead());

		}

		@Override
		public int onEnd() {
			reset();
			if (!doughQueue.isEmpty()) {
				myAgent.addBehaviour(this);
				return super.onEnd();
			}
			requestKneadingRunning = false;
			return 0;

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
				kneadingRequest.setProtocol(Protocols.KNEAD);
				request = getJSONMessage(doughQueue.element());
				kneadingRequest.setContent(request);
				kneadingRequest.setLanguage("English");
				kneadingRequest.setOntology("Bakery-order-ontology");
				myAgent.send(kneadingRequest);
				kneadingMachineFree[myFreeMachine] = false;
				System.out.println("Set machine " + myFreeMachine + " to occupied.");
				doughQueue.remove();

			}

		}

	}

	private class ReceiveKneadedDough extends CyclicBehaviour {

		private String response;
		private AID kneadingAgentId;

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.KNEAD);
			ACLMessage msg = myAgent.receive(msgTemplate);
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

				// sending the kneaded dough to the factory
				ACLMessage doughReady = new ACLMessage(ACLMessage.INFORM);
				doughReady.addReceiver(DoughFactoryAgentId);
				doughReady.setProtocol(Protocols.DOUGH);
				doughReady.setContent(response);
				doughReady.setLanguage("English");
				doughReady.setOntology("Bakery-order-ontology");
				myAgent.send(doughReady);
				System.out.println("Dough for " + response + " is ready.");

			} else {
				block();
			}

		}

	}
}

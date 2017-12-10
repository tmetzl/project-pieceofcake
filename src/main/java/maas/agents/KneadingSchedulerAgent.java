package maas.agents;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONObject;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.config.Protocols;
import maas.utils.KneadingInfo;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends SynchronizedAgent {

	private AID[] kneadingAgents;
	private boolean[] kneadingMachineFree;
	private Queue<KneadingInfo> doughQueue = new LinkedList<>();
	private AID doughFactoryAgentId;
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
		String product = dough.getProductName();
		long kneadingTime = dough.getKneadingTime();
		return String.format("{\"%s\":%d}", product, kneadingTime);
	}

	@Override
	protected void setup() {
		super.setup();
		// Printout a welcome message
		String welcomeMessage = String.format("Kneading-Scheduler %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock());
		seq.addSubBehaviour(new WaitForStart());

		addBehaviour(seq);
		addBehaviour(new ReceiveDoughMessage());
		addBehaviour(new ReceiveKneadedDough());

	}

	@Override
	protected void takeDown() {
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

	private class ReceiveDoughMessage extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.DOUGH);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				String doughRequest = msg.getContent();
				doughFactoryAgentId = msg.getSender();
				JSONObject obj = new JSONObject(doughRequest);
				String[] names = JSONObject.getNames(obj);

				for (int i = 0; i < names.length; i++) {
					KneadingInfo dough = new KneadingInfo();
					dough.setProductName(names[i]);
					dough.setKneadingTime(obj.getLong(names[i]));
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
				String message = String.format("Set machine %d to occupied.", myFreeMachine);
				logger.log(Logger.INFO, message);
				doughQueue.remove();

			}

		}

	}

	private class ReceiveKneadedDough extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.KNEAD);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				String response = msg.getContent();
				AID kneadingAgentId = msg.getSender();
				// setting the agent to be free
				for (int i = 0; i < kneadingAgents.length; i++) {
					if (kneadingAgents[i].equals(kneadingAgentId)) {
						kneadingMachineFree[i] = true;
						String message = String.format("Set machine %d to free.", i);
						logger.log(Logger.INFO, message);
						break;
					}
				}

				// sending the kneaded dough to the factory
				ACLMessage doughReady = new ACLMessage(ACLMessage.INFORM);
				doughReady.addReceiver(doughFactoryAgentId);
				doughReady.setProtocol(Protocols.DOUGH);
				doughReady.setContent(response);
				doughReady.setLanguage("English");
				doughReady.setOntology("Bakery-order-ontology");
				myAgent.send(doughReady);
				String message = String.format("Dough for %s is ready.", response);
				logger.log(Logger.INFO, message);

			} else {
				block();
			}

		}

	}
}

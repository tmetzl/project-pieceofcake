package maas.agents;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
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
import maas.interfaces.BakeryObserver;
import maas.objects.Bakery;
import maas.objects.KneadingInfo;
import maas.objects.Order;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends SynchronizedAgent implements BakeryObserver {

	private AID[] kneadingAgents;
	private boolean[] kneadingMachineFree;
	private Queue<KneadingInfo> doughQueue = new LinkedList<>();
	private boolean requestKneadingRunning = false;
	private Bakery myBakery;
	private List<Order> ordersOfDay; 

	public KneadingSchedulerAgent(String[] kneadingAgentNames, Bakery bakery) {
		this.kneadingAgents = new AID[kneadingAgentNames.length];
		for (int i = 0; i < kneadingAgentNames.length; i++) {
			this.kneadingAgents[i] = new AID(kneadingAgentNames[i], AID.ISLOCALNAME);
		}
		this.kneadingMachineFree = new boolean[kneadingAgents.length];
		Arrays.fill(kneadingMachineFree, true);
		this.myBakery = bakery;

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
	
	@Override
	public void notifyObserver(String topic) {
		int day = getDay();
		List<Order> currentOrders = myBakery.getOrdersOfDay(day);
		if (currentOrders.size() != ordersOfDay.size()) {
			ordersOfDay = currentOrders;
			// TODO update the dough queue
		}
	}

	private class ReceiveDoughMessage extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.DOUGH);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
				String doughRequest = msg.getContent();
				JSONArray obj = new JSONArray(doughRequest);

				for (int i = 0; i < obj.length(); i++) {
					KneadingInfo dough = new KneadingInfo();
					dough.fromJSONMessage(obj.getJSONObject(i));
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
				request = doughQueue.element().toJSONMessage();
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
				KneadingInfo dough = new KneadingInfo();
				dough.fromJSONMessage(new JSONObject(response));
				myAgent.addBehaviour(new RestDough(dough));
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

			} else {
				block();
			}

		}

	}

	private class RestDough extends Behaviour {

		private boolean restingFinished = false;
		private long startingTime;
		private KneadingInfo dough;

		public RestDough(KneadingInfo dough) {
			this.dough = dough;
		}

		@Override
		public void onStart() {
			this.startingTime = System.currentTimeMillis();

		}

		@Override
		public void action() {
			long currentTime = System.currentTimeMillis();
			long remainingTime = dough.getRestingTime() - (currentTime - startingTime);
			if (remainingTime <= 0) {
				restingFinished = true;
				myBakery.updateDoughList(dough.getProductName());
			} else {
				block(remainingTime);
			}

		}

		@Override
		public boolean done() {
			return restingFinished;
		}
	}
}

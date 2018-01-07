package maas.agents;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.json.JSONObject;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.behaviours.SynchronizeClock;
import maas.behaviours.ReceiveStartingTime;
import maas.config.Protocols;
import maas.config.Topic;
import maas.interfaces.BakeryObserver;
import maas.objects.Bakery;
import maas.objects.KneadingInfo;
import maas.objects.Order;
import maas.objects.Product;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends SynchronizedAgent implements BakeryObserver {

	private String[] kneadingAgentNames;
	private AID[] kneadingAgents;
	private boolean[] kneadingMachineFree;
	private Queue<KneadingInfo> doughQueue;
	private Set<String> doughInProcess;
	private boolean requestKneadingRunning;
	private Bakery myBakery;
	private List<Order> ordersOfDay;

	public KneadingSchedulerAgent(String[] kneadingAgentNames, Bakery bakery) {
		this.kneadingAgentNames = kneadingAgentNames;
		this.kneadingAgents = new AID[kneadingAgentNames.length];
		this.kneadingMachineFree = new boolean[kneadingAgents.length];
		Arrays.fill(kneadingMachineFree, true);
		this.myBakery = bakery;
		this.location = bakery.getLocation();
		this.doughQueue = new LinkedList<>();
		this.doughInProcess = new HashSet<>();
		this.requestKneadingRunning = false;
		this.ordersOfDay = new LinkedList<>();
		myBakery.registerObserver(this, Topic.DAILY_ORDERS);

	}

	@Override
	protected void setup() {
		this.kneadingAgents = new AID[kneadingAgentNames.length];
		for (int i = 0; i < kneadingAgentNames.length; i++) {
			this.kneadingAgents[i] = new AID(kneadingAgentNames[i], AID.ISLOCALNAME);
		}
		// Printout a welcome message
		String welcomeMessage = String.format("Kneading-Scheduler %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));

		addBehaviour(seq);
		addBehaviour(new ReceiveKneadedDough());

	}

	@Override
	protected void takeDown() {
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

	@Override
	public void notifyObserver(String topic) {
		int day = getScenarioClock().getDate().getDay();
		List<Order> currentOrders = myBakery.getOrdersOfDay(day);
		if (currentOrders != null && currentOrders.size() != ordersOfDay.size()) {
			ordersOfDay = currentOrders;
			updateDoughQueue();
		}
	}

	private void updateDoughQueue() {
		Set<String> differentProducts = new HashSet<>();
		for (Order order : ordersOfDay) {
			String[] productNames = order.getProductIds();
			for (String product : productNames) {
				KneadingInfo dough = new KneadingInfo();
				dough.setProductName(product);
				if (!myBakery.isDoughInStock(product) && !doughInProcess.contains(product)
						&& !doughQueue.contains(dough)) {
					differentProducts.add(product);
				}
			}
		}
		for (String productName : differentProducts) {
			KneadingInfo dough = new KneadingInfo();
			Product product = myBakery.getProductByName(productName);
			dough.setProductName(productName);
			dough.setKneadingTime(product.getDoughPrepTime());
			dough.setRestingTime(product.getDoughRestingTime());
			doughQueue.add(dough);
		}
		if (!requestKneadingRunning) {
			addBehaviour(new RequestKneading());
			requestKneadingRunning = true;
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
				doughInProcess.add(doughQueue.element().getProductName());
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
				doughInProcess.remove(dough.getProductName());
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

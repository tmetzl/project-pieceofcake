package maas.agents;

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
	private transient Bakery myBakery;
	private Logger logger;
	private List<Order> todaysOrders;

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Kneading-Scheduler " + getAID().getName() + " is ready.");
		addBehaviour(new ProcessDoughPreparation());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class ProcessDoughPreparation extends SequentialBehaviour {

		private Product product;
		private String request;
		private long kneadingTime;
		private long restingTime;
		private long prepTime;
		private String productId;

		public ProcessDoughPreparation() {

			this.addSubBehaviour(new getRequestInfoToPrepare());
			this.addSubBehaviour(new SendKneadingRequest());
			this.addSubBehaviour(new ReceiveKneadedDough());
			this.addSubBehaviour(new Rest(restingTime));
			this.addSubBehaviour(new Prepare(prepTime));

		}

		private class getRequestInfoToPrepare extends OneShotBehaviour {

			@Override
			public void action() {
				productId = product.getId();
				kneadingTime = product.getDoughPrepTime();
				restingTime = product.getDoughRestingTime();
				prepTime = product.getItemPrepTime();
				// maybe a better way????
				request = String.valueOf(kneadingTime);
			}

		}

		private class SendKneadingRequest extends OneShotBehaviour {

			@Override
			public void action() {
				ACLMessage kneadingRequest = new ACLMessage(ACLMessage.INFORM);
				// allow for more than one machine???
				kneadingRequest.addReceiver(kneadingAgents[0]);
				kneadingRequest.setContent(request);
				kneadingRequest.setLanguage("English");
				kneadingRequest.setOntology("Bakery-order-ontology");
				myAgent.send(kneadingRequest);

			}

		}

		private class ReceiveKneadedDough extends Behaviour {

			private boolean doughReceived = false;

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
					doughReceived = true;

				} else {
					block();
				}

			}

			@Override
			public boolean done() {
				return doughReceived;
			}

		}

		private class Rest extends Behaviour {

			private long startingTime;
			private long restingTime;
			private boolean restingFinished = false;

			// dont know if this is needed???
			public Rest(long restingTime) {
				this.restingTime = restingTime;
			}

			@Override
			public void onStart() {
				this.startingTime = System.currentTimeMillis();

			}

			@Override
			public void action() {
				long currentTime = System.currentTimeMillis();
				long remainingTime = restingTime - (currentTime - startingTime);
				if (remainingTime >= 0) {
					restingFinished = true;
				} else {
					block(remainingTime);
				}

			}

			@Override
			public boolean done() {
				return restingFinished;
			}

		}

		private class Prepare extends Behaviour {

			private long startingTime;
			private long prepTime;
			private boolean prepFinished = false;

			public Prepare(long prepTime) {
				this.prepTime = prepTime;
			}

			@Override
			public void onStart() {
				this.startingTime = System.currentTimeMillis();

			}

			@Override
			public void action() {
				long currentTime = System.currentTimeMillis();
				long remainingTime = prepTime - (currentTime - startingTime);
				if (remainingTime >= 0) {
					prepFinished = true;
				} else {
					block(remainingTime);
				}

			}

			@Override
			public boolean done() {
				return prepFinished;
			}

		}

	}
}

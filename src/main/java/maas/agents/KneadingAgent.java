package maas.agents;

import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.config.Protocols;

@SuppressWarnings("serial")
public class KneadingAgent extends Agent {

	public long getKneadingTime(String message) {
		JSONObject obj = new JSONObject(message);
		String[] keys = JSONObject.getNames(obj);
		long kneadingTime = obj.getLong(keys[0]);
		return kneadingTime;
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Kneading-Machine " + getAID().getName() + " is ready.");
		addBehaviour(new ProcessKneadingRequest());
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	private class ProcessKneadingRequest extends SequentialBehaviour {

		private AID kneadingScheduler;
		private String request;
		private long kneadingTime;

		public ProcessKneadingRequest() {
			this.addSubBehaviour(new ReceiveKneadingRequest());
			this.addSubBehaviour(new Knead());
			this.addSubBehaviour(new RespondToKneadingRequest());
		}

		@Override
		public int onEnd() {
			reset();
			myAgent.addBehaviour(this);
			return super.onEnd();
		}

		private class ReceiveKneadingRequest extends Behaviour {

			private boolean requestReceived = false;

			@Override
			public void action() {
				MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.KNEAD);
				ACLMessage msg = myAgent.receive(msgTemplate);
				if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
					request = msg.getContent();
					kneadingScheduler = msg.getSender();
					kneadingTime = getKneadingTime(request);
					requestReceived = true;
					System.out.println("Kneading time is " + kneadingTime);

				} else {
					block();
				}

			}

			@Override
			public boolean done() {
				return requestReceived;
			}

		}

		private class Knead extends Behaviour {

			private boolean kneadingFinished = false;
			private long startingTime;

			@Override
			public void onStart() {
				this.startingTime = System.currentTimeMillis();

			}

			@Override
			public void action() {
				long currentTime = System.currentTimeMillis();
				long remainingTime = kneadingTime - (currentTime - startingTime);
				if (remainingTime <= 0) {
					kneadingFinished = true;
				} else {
					block(remainingTime);
				}

			}

			@Override
			public boolean done() {
				return kneadingFinished;
			}

		}

		private class RespondToKneadingRequest extends OneShotBehaviour {

			@Override
			public void action() {
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
				reply.setProtocol(Protocols.KNEAD);
				reply.addReceiver(kneadingScheduler);
				reply.setContent(request);
				reply.setLanguage("English");
				reply.setOntology("Bakery-order-ontology");
				myAgent.send(reply);
				System.out.println("Dough is kneaded.");

			}

		}

	}
}

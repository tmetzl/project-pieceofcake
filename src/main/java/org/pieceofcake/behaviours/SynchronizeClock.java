package org.pieceofcake.behaviours;

import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.ScenarioClock;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SynchronizeClock extends SequentialBehaviour {

	private static final long serialVersionUID = -5408918980853595347L;
	
	private long sendTime;
	private List<AID> timerAgents;
	private ScenarioClock clock;

	public SynchronizeClock(ScenarioClock clock) {
		this.timerAgents = new LinkedList<>();
		this.clock = clock;
		this.addSubBehaviour(new FindAgents(Services.TIME, Services.TIME_NAME, timerAgents));
		this.addSubBehaviour(new SendTimeRequest());
		this.addSubBehaviour(new ReceiveTime());
	}

	private class SendTimeRequest extends OneShotBehaviour {

		private static final long serialVersionUID = 3020688254149494435L;

		@Override
		public void action() {
			ACLMessage timeRequest = new ACLMessage(ACLMessage.REQUEST);
			timeRequest.addReceiver(timerAgents.get(0));
			timeRequest.setLanguage("English");
			timeRequest.setOntology("Bakery-order-ontology");
			timeRequest.setProtocol(Protocols.TIME);
			sendTime = clock.getSynchronizedTime();
			timeRequest.setContent(String.valueOf(sendTime));
			myAgent.send(timeRequest);
		}

	}

	private class ReceiveTime extends Behaviour {

		private static final long serialVersionUID = 1654641852358212361L;
		
		private boolean replyReceived = false;

		@Override
		public void action() {
			MessageTemplate matchSender = MessageTemplate.MatchSender(timerAgents.get(0));
			MessageTemplate matchProtocol = MessageTemplate.MatchProtocol(Protocols.TIME);
			MessageTemplate msgTemplate = MessageTemplate.and(matchSender, matchProtocol);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				long receiveTime = System.currentTimeMillis();
				long timerAgentTime = Long.parseLong(msg.getContent());
				// Calculate the round trip time RTT
				long roundTripTime = receiveTime - sendTime;
				long expectedTime = timerAgentTime + roundTripTime / 2l;
				long timeOffset = expectedTime - receiveTime;
				clock.setTimeOffset(timeOffset);
				replyReceived = true;
			} else {
				block();
			}
		}

		@Override
		public boolean done() {
			return replyReceived;
		}

	}

}
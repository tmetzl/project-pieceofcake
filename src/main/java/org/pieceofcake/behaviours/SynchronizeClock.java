package org.pieceofcake.behaviours;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.objects.ScenarioClock;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class SynchronizeClock extends SequentialBehaviour {

	private static final long serialVersionUID = -5408918980853595347L;
	
	private Logger logger;
	private long sendTime;
	private AID timerAgent;
	private ScenarioClock clock;

	public SynchronizeClock(ScenarioClock clock) {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		this.clock = clock;
		this.addSubBehaviour(new FindTimerAgent());
		this.addSubBehaviour(new SendTimeRequest());
		this.addSubBehaviour(new ReceiveTime());
	}

	private class FindTimerAgent extends Behaviour {

		private static final long serialVersionUID = 4518440444012270787L;
		
		private boolean foundTimerAgent;

		public FindTimerAgent() {
			foundTimerAgent = false;
		}

		@Override
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("time");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				if (result.length > 0) {
					timerAgent = result[0].getName();
					foundTimerAgent = true;

				}
			} catch (FIPAException fe) {
				logger.log(Logger.WARNING, fe.getMessage(), fe);
			}
		}

		@Override
		public boolean done() {
			return foundTimerAgent;
		}

	}

	private class SendTimeRequest extends OneShotBehaviour {

		private static final long serialVersionUID = 3020688254149494435L;

		@Override
		public void action() {
			ACLMessage timeRequest = new ACLMessage(ACLMessage.REQUEST);
			timeRequest.addReceiver(timerAgent);
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
			MessageTemplate matchSender = MessageTemplate.MatchSender(timerAgent);
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
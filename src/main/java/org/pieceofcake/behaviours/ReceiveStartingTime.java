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

public class ReceiveStartingTime extends SequentialBehaviour {

	private static final long serialVersionUID = 1079848084822791821L;
	
	private Logger logger;
	private long sendTime;
	private AID startUpAgent;
	private ScenarioClock clock;

	public ReceiveStartingTime(ScenarioClock clock) {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		this.clock = clock;
		this.addSubBehaviour(new FindStartUpAgent());
		this.addSubBehaviour(new SendStartUpTimeRequest());
		this.addSubBehaviour(new ReceiveStartUpTime());
	}

	private class FindStartUpAgent extends Behaviour {

		private static final long serialVersionUID = -3826773622991660902L;
		
		private boolean foundStartUpAgent;

		public FindStartUpAgent() {
			foundStartUpAgent = false;
		}

		@Override
		public void action() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("startup");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				if (result.length > 0) {
					startUpAgent = result[0].getName();
					foundStartUpAgent = true;
				}
			} catch (FIPAException fe) {
				logger.log(Logger.WARNING, fe.getMessage(), fe);
			}
		}

		@Override
		public boolean done() {
			return foundStartUpAgent;
		}

	}

	private class SendStartUpTimeRequest extends OneShotBehaviour {

		private static final long serialVersionUID = -1039434852598599267L;

		@Override
		public void action() {
			ACLMessage startUpRequest = new ACLMessage(ACLMessage.REQUEST);
			startUpRequest.addReceiver(startUpAgent);
			startUpRequest.setLanguage("English");
			startUpRequest.setOntology("Bakery-order-ontology");
			startUpRequest.setProtocol(Protocols.STARTUP);
			sendTime = clock.getSynchronizedTime();
			startUpRequest.setContent(String.valueOf(sendTime));
			myAgent.send(startUpRequest);
		}

	}

	private class ReceiveStartUpTime extends Behaviour {

		private static final long serialVersionUID = 3277250304669562301L;
		
		private boolean replyReceived = false;

		@Override
		public void action() {
			MessageTemplate matchSender = MessageTemplate.MatchSender(startUpAgent);
			MessageTemplate matchProtocol = MessageTemplate.MatchProtocol(Protocols.STARTUP);
			MessageTemplate msgTemplate = MessageTemplate.and(matchSender, matchProtocol);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				long startingTime = Long.parseLong(msg.getContent());
				clock.setStartingTime(startingTime);
				replyReceived = true;
				long remainingTime = (startingTime - clock.getSynchronizedTime())/1000;
				String logMsg = String.format("Starting in %ds.", remainingTime);
				logger.log(Logger.INFO, logMsg);
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
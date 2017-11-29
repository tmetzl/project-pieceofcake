package maas.agents;

import jade.core.AID;
import jade.core.Agent;
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
import maas.config.Protocols;

@SuppressWarnings("serial")
public class SynchronizedAgent extends Agent {

	long timeOffset = 0l;
	long startUpTime;
	AID timerAgent;
	Logger logger;

	@Override
	protected void setup() {
		logger = Logger.getJADELogger(this.getClass().getName());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("startable");
		sd.setName("synchronized-agent");
		sd.addProtocols(Protocols.STARTUP);
		registerService(sd);
	}

	protected void registerService(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		try {
			DFAgentDescription[] agentDescription = DFService.search(this, dfd);
			if (agentDescription.length > 0) {
				dfd = agentDescription[0];
				dfd.addServices(sd);
				DFService.modify(this, dfd);
			} else {
				dfd.addServices(sd);
				DFService.register(this, dfd);
			}
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
	}

	@Override
	protected void takeDown() {
		// Remove from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

	protected long getSynchronizedTime() {
		return System.currentTimeMillis() + timeOffset;
	}

	protected long getScenarioTime() {
		return (getSynchronizedTime() - startUpTime) / 1000l;
	}

	class SynchronizeClock extends SequentialBehaviour {

		private long sendTime;

		public SynchronizeClock() {
			this.addSubBehaviour(new FindTimerAgent());
			this.addSubBehaviour(new SendTimeRequest());
			this.addSubBehaviour(new ReceiveTime());
		}

		private class FindTimerAgent extends Behaviour {

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

			@Override
			public void action() {
				ACLMessage timeRequest = new ACLMessage(ACLMessage.REQUEST);
				timeRequest.addReceiver(timerAgent);
				timeRequest.setLanguage("English");
				timeRequest.setOntology("Bakery-order-ontology");
				sendTime = System.currentTimeMillis();
				timeRequest.setContent(String.valueOf(sendTime));
				myAgent.send(timeRequest);
			}

		}

		private class ReceiveTime extends Behaviour {

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
					timeOffset = expectedTime - receiveTime;
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

	class WaitForStart extends SequentialBehaviour {

		public WaitForStart() {
			this.addSubBehaviour(new ReceiveStartingTime());
			this.addSubBehaviour(new DelayUntilStart());
		}

		class ReceiveStartingTime extends Behaviour {

			private boolean startingTimeReceived = false;

			@Override
			public void action() {

				MessageTemplate startUpMessageTemplate = MessageTemplate.MatchProtocol(Protocols.STARTUP);
				ACLMessage msg = myAgent.receive(startUpMessageTemplate);
				if (msg != null) {
					startUpTime = Long.parseLong(msg.getContent());
					long remainingTime = startUpTime - getSynchronizedTime();
					logger.log(Logger.INFO, myAgent.getLocalName() + ": Starting in " + remainingTime +" ms.");
					startingTimeReceived = true;
				} else {
					block();
				}

			}

			@Override
			public boolean done() {
				return startingTimeReceived;
			}

		}

		private class DelayUntilStart extends Behaviour {

			private boolean waitingFinished = false;

			@Override
			public void action() {
				long currentTime = getSynchronizedTime();
				long remainingTime = startUpTime - currentTime;

				if (currentTime >= startUpTime) {
					logger.log(Logger.INFO, "Started.");
					waitingFinished = true;
				} else {
					block(remainingTime);
				}

			}

			@Override
			public boolean done() {
				return waitingFinished;
			}

		}
	}

}

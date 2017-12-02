package maas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.config.Protocols;

@SuppressWarnings("serial")
public class StartUpAgent extends Agent {

	private Logger logger;

	@Override
	protected void setup() {
		logger = Logger.getJADELogger(this.getClass().getName());

		// Printout a welcome message
		String welcomeMessage = String.format("StartUp agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		addBehaviour(new StartUp());

	}
	
	@Override
	protected void takeDown() {
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

	private class StartUp extends SequentialBehaviour {

		private AID[] agents;

		public StartUp() {
			this.addSubBehaviour(new FindSynchronizedAgents());
			this.addSubBehaviour(new ScheduleStart(5000));
		}

		private class FindSynchronizedAgents extends OneShotBehaviour {

			@Override
			public void action() {

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("startable");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					agents = new AID[result.length];
					for (int i = 0; i < result.length; i++) {
						agents[i] = result[i].getName();
					}
				} catch (FIPAException fe) {
					logger.log(Logger.WARNING, fe.getMessage(), fe);
				}

			}

		}

		private class ScheduleStart extends OneShotBehaviour {

			private long startUpDelay;

			public ScheduleStart(long startUpDelay) {
				this.startUpDelay = startUpDelay;
			}

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				// Add all known agents as receivers
				for (int i = 0; i < agents.length; i++) {
					msg.addReceiver(agents[i]);
				}
				msg.setLanguage("English");
				msg.setProtocol(Protocols.STARTUP);
				long startUpTime = System.currentTimeMillis() + startUpDelay;
				msg.setContent(String.valueOf(startUpTime));
				myAgent.send(msg);
			}

		}

	}

}

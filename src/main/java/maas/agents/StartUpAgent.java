package maas.agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.config.Protocols;
import maas.objects.ScenarioClock;

@SuppressWarnings("serial")
public class StartUpAgent extends Agent {

	private Logger logger;
	private long startUpTime;
	private long shutDownTime;
	private int durationDays = 5;

	public StartUpAgent(int durationDays) {
		this.durationDays = durationDays + 1;
	}

	@Override
	protected void setup() {
		logger = Logger.getJADELogger(this.getClass().getName());

		// Printout a welcome message
		String welcomeMessage = String.format("StartUp agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		try {
			Thread.sleep(5000);
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
			this.addSubBehaviour(new WaitForShutDown());
			this.addSubBehaviour(new Shutdown());
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
				msg.setProtocol(Protocols.STARTUP);
				startUpTime = System.currentTimeMillis() + startUpDelay;
				shutDownTime = startUpTime + (durationDays * ScenarioClock.SECONDS_PER_SCENARIO_DAY * 1000l);
				msg.setContent(String.valueOf(startUpTime));
				myAgent.send(msg);
			}

		}

		private class WaitForShutDown extends Behaviour {

			private boolean waitingFinished = false;

			@Override
			public void action() {
				long currentTime = System.currentTimeMillis();
				long remainingTime = shutDownTime - currentTime;

				if (currentTime >= shutDownTime) {
					logger.log(Logger.INFO, "Terminating.");
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

		// Taken from
		// http://www.rickyvanrijn.nl/2017/08/29/how-to-shutdown-jade-agent-platform-programmatically/
		private class Shutdown extends OneShotBehaviour {
			public void action() {
				ACLMessage shutdownMessage = new ACLMessage(ACLMessage.REQUEST);
				Codec codec = new SLCodec();
				myAgent.getContentManager().registerLanguage(codec);
				myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());
				shutdownMessage.addReceiver(myAgent.getAMS());
				shutdownMessage.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
				shutdownMessage.setOntology(JADEManagementOntology.getInstance().getName());
				try {
					myAgent.getContentManager().fillContent(shutdownMessage,
							new Action(myAgent.getAID(), new ShutdownPlatform()));
					myAgent.send(shutdownMessage);
				} catch (Exception e) {
					logger.log(Logger.WARNING, e.getMessage(), e);
				}

			}
		}

	}

}

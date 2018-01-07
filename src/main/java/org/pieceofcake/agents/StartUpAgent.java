package org.pieceofcake.agents;

import org.pieceofcake.behaviours.DelayUntilDate;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.ScenarioClock;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class StartUpAgent extends Agent {

	private static final long serialVersionUID = 6413837519984937564L;
	private static final long STARTUP_DELAY = 10000;

	private Logger logger;
	private ScenarioClock clock;
	private long startUpTime;
	private Date shutdownDate;

	public StartUpAgent(int durationDays) {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		this.clock = new ScenarioClock();
		this.shutdownDate = new Date(durationDays + 1, 0, 0, 0);
	}

	@Override
	protected void setup() {
		// Register the startup service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("startup");
		sd.setName("startup-service");
		sd.addProtocols(Protocols.STARTUP);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(clock));
		seq.addSubBehaviour(new SetStartUpTime());
		seq.addSubBehaviour(new StartUpAndShutdownService());

		addBehaviour(seq);
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

	private class SetStartUpTime extends OneShotBehaviour {

		private static final long serialVersionUID = 7291131840722708144L;

		@Override
		public void action() {
			startUpTime = clock.getSynchronizedTime() + STARTUP_DELAY;
			clock.setStartingTime(startUpTime);
		}

	}

	private class StartUpAndShutdownService extends ParallelBehaviour {

		private static final long serialVersionUID = -289358804844017706L;

		public StartUpAndShutdownService() {
			this.addSubBehaviour(new HandleStartUpRequest());
			SequentialBehaviour seq = new SequentialBehaviour();
			seq.addSubBehaviour(new DelayUntilDate(clock, shutdownDate));
			seq.addSubBehaviour(new Shutdown());
			this.addSubBehaviour(seq);
		}

		private class HandleStartUpRequest extends CyclicBehaviour {

			private static final long serialVersionUID = 6028793880415341524L;

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate.MatchProtocol(Protocols.STARTUP);
				ACLMessage msg = myAgent.receive(template);
				if (msg != null) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setProtocol(Protocols.STARTUP);
					reply.setContent(String.valueOf(startUpTime));
					myAgent.send(reply);
				} else {
					block();
				}

			}

		}

		// Taken from
		// http://www.rickyvanrijn.nl/2017/08/29/how-to-shutdown-jade-agent-platform-programmatically/
		private class Shutdown extends OneShotBehaviour {
			
			private static final long serialVersionUID = 3437305896195050500L;

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

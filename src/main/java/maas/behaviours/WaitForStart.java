package maas.behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import maas.config.Protocols;
import maas.objects.ScenarioClock;

public class WaitForStart extends SequentialBehaviour {
	
	private static final long serialVersionUID = -4710246411822607838L;
	
	private Logger logger;
	private ScenarioClock clock;
	private long startUpTime;

	public WaitForStart(ScenarioClock clock) {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		this.clock = clock;
		this.addSubBehaviour(new ReceiveStartingTime());
		this.addSubBehaviour(new DelayUntilStart());
	}

	private class ReceiveStartingTime extends Behaviour {

		private static final long serialVersionUID = -8896262283057758970L;
		
		private boolean startingTimeReceived = false;

		@Override
		public void action() {

			MessageTemplate startUpMessageTemplate = MessageTemplate.MatchProtocol(Protocols.STARTUP);
			ACLMessage msg = myAgent.receive(startUpMessageTemplate);
			if (msg != null) {
				startUpTime = Long.parseLong(msg.getContent());
				long remainingTime = startUpTime - clock.getSynchronizedTime();
				String output = String.format("Starting in %d ms.", remainingTime);
				logger.log(Logger.INFO, output);
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

		private static final long serialVersionUID = 2540516993766759173L;
		
		private boolean waitingFinished = false;

		@Override
		public void action() {
			long currentTime = clock.getSynchronizedTime();
			long remainingTime = startUpTime - currentTime;

			if (currentTime >= startUpTime) {
				clock.start();
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
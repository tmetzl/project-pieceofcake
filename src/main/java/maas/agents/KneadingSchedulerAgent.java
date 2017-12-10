package maas.agents;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends SynchronizedAgent {

	private AID[] kneadingAgents;

	@Override
	protected void setup() {
		super.setup();
		// Printout a welcome message
		String welcomeMessage = String.format("Kneading-Scheduler %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock());
		seq.addSubBehaviour(new WaitForStart());

		addBehaviour(seq);
	}

	@Override
	protected void takeDown() {
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}

}

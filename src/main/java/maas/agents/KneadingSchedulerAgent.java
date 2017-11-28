package maas.agents;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends SynchronizedAgent {

	private AID[] kneadingAgents;

	@Override
	protected void setup() {
		super.setup();
		// Printout a welcome message
		System.out.println("Hello! Kneading-Scheduler " + getAID().getName() + " is ready.");
		
		SequentialBehaviour seq = new SequentialBehaviour();
		
		seq.addSubBehaviour(new SynchronizeClock());
		seq.addSubBehaviour(new WaitForStart());

		addBehaviour(seq);
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

}

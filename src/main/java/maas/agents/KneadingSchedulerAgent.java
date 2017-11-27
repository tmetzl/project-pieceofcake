package maas.agents;

import jade.core.AID;
import jade.core.Agent;

@SuppressWarnings("serial")
public class KneadingSchedulerAgent extends Agent {

	private AID[] kneadingAgents;

	@Override
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Kneading-Scheduler " + getAID().getName() + " is ready.");
	}

	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

}

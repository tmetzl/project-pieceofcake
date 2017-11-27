package maas.agents;

import jade.core.AID;
import jade.core.Agent;

public class KneadingSchedulerAgent extends Agent {
	
	private AID[] kneadingAgents;
	
	
	@Override
	protected void setup() {
		
	}
	
	@Override
	protected void takeDown() {
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

}


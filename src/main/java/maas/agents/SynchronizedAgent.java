package maas.agents;

import jade.core.Agent;
import jade.util.Logger;
import maas.interfaces.Localizable;
import maas.interfaces.Startable;
import maas.objects.Location;
import maas.objects.ScenarioClock;

@SuppressWarnings("serial")
public class SynchronizedAgent extends Agent implements Localizable, Startable {

	protected Logger logger;
	protected Location location;
	private ScenarioClock clock;
	
	public SynchronizedAgent() {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		this.clock = new ScenarioClock();
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	@Override
	public ScenarioClock getScenarioClock() {
		return clock;
	}

	@Override
	protected void takeDown() {
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}
	
}

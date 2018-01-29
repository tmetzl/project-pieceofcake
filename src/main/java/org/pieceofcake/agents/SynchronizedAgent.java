package org.pieceofcake.agents;

import org.pieceofcake.interfaces.Localizable;
import org.pieceofcake.interfaces.Startable;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.ScenarioClock;

import jade.core.Agent;
import jade.util.Logger;

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

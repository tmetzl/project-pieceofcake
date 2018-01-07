package org.pieceofcake.agents;

import org.pieceofcake.streetnetwork.DiGraph;

import jade.core.Agent;
import jade.util.Logger;

public class GPSAgent extends Agent {
	
	private static final long serialVersionUID = -5382446252884625003L;
	
	private DiGraph streetNetwork;
	private Logger logger;
	
	public GPSAgent(DiGraph streetNetwork) {
		this.streetNetwork = streetNetwork;
	}
	
	@Override
	protected void setup() {
		logger = Logger.getJADELogger(this.getClass().getName());
		// Printout a welcome message
		String welcomeMessage = String.format("GPS Agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);
	}

}

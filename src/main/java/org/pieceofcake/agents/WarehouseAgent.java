package org.pieceofcake.agents;

import org.pieceofcake.behaviours.DelayUntilDate;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.ResourceHandler;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Warehouse;

import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class WarehouseAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -5135767825700301917L;

	private String bakeryName;
	private Warehouse warehouse;
	
	public WarehouseAgent(Location location, String bakeryName) {
		this.bakeryName = bakeryName;
		this.location = location;
		this.warehouse = new Warehouse();
	}

	@Override
	protected void setup() {
		// Register the warehouse as a service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.RESOURCE);
		sd.setName(bakeryName);
		sd.addProtocols(Protocols.RESOURCE);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		
		// Printout a welcome message
		String welcomeMessage = String.format("Warehouse-Agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		seq.addSubBehaviour(new MonitorTime(new Date(1, 0, 0, 0)));

		addBehaviour(seq);
		addBehaviour(new ResourceHandler(warehouse));
	}

	public void newDay() {
		warehouse.clear();
	}
	
	private class MonitorTime extends SequentialBehaviour {

		private static final long serialVersionUID = -7393395145982480330L;

		private Date date;

		public MonitorTime(Date date) {
			this.date = date;
			this.addSubBehaviour(new DelayUntilDate(getScenarioClock(), date));
		}

		@Override
		public int onEnd() {
			newDay();
			String message = String.format("Day is now %d.", date.getDay());
			logger.log(Logger.INFO, message);
			myAgent.addBehaviour(new MonitorTime(new Date(date.getDay() + 1, 0, 0, 0)));
			return 0;
		}

	}

}

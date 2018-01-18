package org.pieceofcake.behaviours;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class FindAgent extends Behaviour {

	private static final long serialVersionUID = 1L;

	private Logger logger;
	private boolean foundAgent;
	private ServiceDescription sd;

	public FindAgent(String service) {
		foundAgent = false;
		sd = new ServiceDescription();
		// e.g. service = Services.BAKE
		sd.setType(service);
	}

	@Override
	public void action() {
		DFAgentDescription template = new DFAgentDescription();
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			if (result.length > 0) {
				// startUpAgent = result[0].getName();
				foundAgent = true;
			}
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
	}

	@Override
	public boolean done() {
		return foundAgent;
	}

}

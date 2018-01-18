package org.pieceofcake.behaviours;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class FindAgents extends Behaviour {

	private static final long serialVersionUID = -3166494254968409194L;

	private Logger logger;
	private boolean foundAgent;
	private ServiceDescription sd;
	private List<AID> agentList;

	public FindAgents(String serviceType, String serviceName, List<AID> emptyList) {
		this.logger = Logger.getJADELogger(this.getClass().getName());
		foundAgent = false;
		sd = new ServiceDescription();
		// e.g. service = Services.BAKE
		sd.setType(serviceType);
		// bakery name as serviceName
		sd.setName(serviceName);
		this.agentList = emptyList;
	}

	@Override
	public void action() {
		DFAgentDescription template = new DFAgentDescription();
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			if (result.length > 0) {
				for (int i = 0; i < result.length; i++) {
					agentList.add(result[i].getName());
				}
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

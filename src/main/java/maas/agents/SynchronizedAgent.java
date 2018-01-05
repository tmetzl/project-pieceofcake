package maas.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
import maas.config.Protocols;
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
	protected void setup() {	
		// Register agent as startable
		ServiceDescription sd = new ServiceDescription();
		sd.setType("startable");
		sd.setName("synchronized-agent");
		sd.addProtocols(Protocols.STARTUP);
		registerService(sd);
	}

	protected void registerService(ServiceDescription sd) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		try {
			DFAgentDescription[] agentDescription = DFService.search(this, dfd);
			if (agentDescription.length > 0) {
				dfd = agentDescription[0];
				dfd.addServices(sd);
				DFService.modify(this, dfd);
			} else {
				dfd.addServices(sd);
				DFService.register(this, dfd);
			}
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
	}

	@Override
	protected void takeDown() {
		// Remove from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}
	
}

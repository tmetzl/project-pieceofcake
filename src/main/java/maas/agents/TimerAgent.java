package maas.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import maas.config.Protocols;

/**
 * Timer Agent
 * 
 * @author Tim Metzler
 *
 */
@SuppressWarnings("serial")
public class TimerAgent extends Agent {

	private static TimerAgent instance;
	private Logger logger;
	
	private TimerAgent() {
		
	}
	
	public static TimerAgent getInstance() {
		if (instance == null) {
			instance = new TimerAgent();
		}
		return instance;
	}

	@Override
	protected void setup() {

		logger = Logger.getJADELogger(this.getClass().getName());

		// Printout a welcome message
		System.out.println("Hello! Timer-agent " + getAID().getName() + " is ready.");
		
		// Register the bakery service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("time");
		sd.setName("time-service");
		sd.addProtocols(Protocols.TIME);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		
		addBehaviour(new HandleTimeRequest());
	}
	
	@Override
	protected void takeDown() {
		// Remove from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}
	
	private class HandleTimeRequest extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setProtocol(Protocols.TIME);
				reply.setContent(String.valueOf(System.currentTimeMillis()));
				myAgent.send(reply);
			} else {
				block();
			}
			
		}
		
	}

}

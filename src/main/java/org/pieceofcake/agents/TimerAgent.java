package org.pieceofcake.agents;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

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
		String welcomeMessage = String.format("Timer agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		
		// Register the timer agent service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.TIME);
		sd.setName(Services.TIME_NAME);
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
		logger.log(Logger.INFO, getAID().getLocalName() + ": Terminating.");
	}
	
	private class HandleTimeRequest extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate template = MessageTemplate.MatchProtocol(Protocols.TIME);
			ACLMessage msg = myAgent.receive(template);
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

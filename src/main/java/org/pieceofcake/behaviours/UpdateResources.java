package org.pieceofcake.behaviours;

import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.Resource;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class UpdateResources extends SequentialBehaviour {

	private static final long serialVersionUID = 4291780213977508831L;
	
	private Resource resource;
	private List<AID> warehouses;
	
	public UpdateResources(Resource resource, String bakeryName) {
		this.resource = resource;
		this.warehouses = new LinkedList<>();
		this.addSubBehaviour(new FindAgents(Services.RESOURCE, bakeryName, warehouses));
		this.addSubBehaviour(new MessageWarehouse());
	}

	private class MessageWarehouse extends OneShotBehaviour {

		private static final long serialVersionUID = 1229489101391172421L;

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol(Protocols.RESOURCE);
			msg.addReceiver(warehouses.get(0));
			msg.setContent(resource.toJSONObject().toString());
			myAgent.send(msg);			
		}
		
	}

}

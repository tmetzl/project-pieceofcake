package org.pieceofcake.behaviours;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.objects.OrderContract;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class NotifyOrderComplete extends OneShotBehaviour {

	private static final long serialVersionUID = -8286186018031867662L;
	
	private OrderContract contract;

	public NotifyOrderComplete(OrderContract contract) {
		this.contract = contract;
	}

	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(contract.getCustomerAgentId());
		msg.setProtocol(Protocols.ORDER_COMPLETE);
		msg.setContent(contract.getOrder().toJSONObject().toString());
		myAgent.send(msg);
	}

}

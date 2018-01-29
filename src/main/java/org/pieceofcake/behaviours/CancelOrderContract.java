package org.pieceofcake.behaviours;

import org.pieceofcake.config.Protocols;
import org.pieceofcake.objects.OrderContract;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class CancelOrderContract extends OneShotBehaviour {

		private static final long serialVersionUID = -5994674106161575658L;
		
		private OrderContract contract;
		
		public CancelOrderContract(OrderContract contract) {
			this.contract = contract;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
			msg.setProtocol(Protocols.CANCEL_ORDER);
			msg.setContent(contract.getOrder().toJSONObject().toString());
			for (AID agent : contract.getAgents()) {
				msg.addReceiver(agent);
			}
			myAgent.send(msg);
			contract = null;
		}

	}
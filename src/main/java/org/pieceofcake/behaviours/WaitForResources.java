package org.pieceofcake.behaviours;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.Resource;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitForResources extends SequentialBehaviour {

	private static final long serialVersionUID = -5788238130592126718L;
	private static long id = 0l;

	private Resource resource;
	private List<AID> bakeryAgents;
	private String requestId;

	public WaitForResources(Resource resource, String bakeryName) {
		this.resource = resource;
		this.bakeryAgents = new LinkedList<>();
		this.requestId = generateRequestId();
		this.addSubBehaviour(new FindAgents(Services.RESOURCE, bakeryName, bakeryAgents));
		ParallelBehaviour par = new ParallelBehaviour();
		par.addSubBehaviour(new Request());
		par.addSubBehaviour(new ReceiveResources());

		this.addSubBehaviour(par);
	}

	public static synchronized String generateRequestId() {
		id += 1;
		return Long.toString(id);
	}

	private class Request extends OneShotBehaviour {

		private static final long serialVersionUID = 5296814150919295466L;

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(bakeryAgents.get(0));
			msg.setProtocol(Protocols.RESOURCE);
			msg.setConversationId(requestId);
			msg.setContent(resource.toJSONObject().toString());
			myAgent.send(msg);
		}

	}

	private class ReceiveResources extends Behaviour {

		private static final long serialVersionUID = 4798751739696680836L;

		private boolean resourcesReceived = false;

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(requestId),
					MessageTemplate.MatchProtocol(Protocols.RESOURCE));
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				Resource receivedResource = new Resource();
				receivedResource.fromJSONObject(new JSONObject(msg.getContent()));
				if (receivedResource.getResourceType().equals(resource.getResourceType())
						&& receivedResource.getProductId().equals(resource.getProductId())
						&& receivedResource.getAmount() == resource.getAmount()) {
					resourcesReceived = true;
				}
			} else {
				block();
			}

		}

		@Override
		public boolean done() {
			return resourcesReceived;
		}

	}

}

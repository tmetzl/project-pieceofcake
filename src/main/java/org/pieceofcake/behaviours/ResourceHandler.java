package org.pieceofcake.behaviours;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.objects.Warehouse;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class ResourceHandler extends CyclicBehaviour {

	private static final long serialVersionUID = -6118994291781057665L;

	private Warehouse warehouse;
	private Map<String, Queue<ResourceRequest>> resourceRequests;

	public ResourceHandler(Warehouse warehouse) {
		this.warehouse = warehouse;
		this.resourceRequests = new ConcurrentHashMap<>();
	}

	@Override
	public void action() {
		MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.RESOURCE);
		ACLMessage msg = myAgent.receive(msgTemplate);
		if (msg != null) {
			Resource resource = new Resource();
			resource.fromJSONObject(new JSONObject(msg.getContent()));
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				ResourceRequest request = new ResourceRequest(msg.getSender(), msg.getConversationId(), resource);
				addToRequestQueue(request);
			} else if (msg.getPerformative() == ACLMessage.INFORM) {
				warehouse.addResource(resource);
				notifyRequesters(resource);
			}
		} else {
			block();
		}

	}

	public void addToRequestQueue(ResourceRequest request) {
		String resourceKey = request.getResource().getResourceType() + request.getResource().getProductId();
		Queue<ResourceRequest> requestQueue = resourceRequests.get(resourceKey);
		if (requestQueue == null) {
			requestQueue = new LinkedList<>();
		}
		requestQueue.add(request);
		resourceRequests.put(resourceKey, requestQueue);
		notifyRequesters(request.getResource());
	}

	public void notifyRequesters(Resource resource) {
		String resourceKey = resource.getResourceType() + resource.getProductId();
		Queue<ResourceRequest> resourceQueue = resourceRequests.computeIfAbsent(resourceKey, k -> new LinkedList<>());
		ResourceRequest request = resourceQueue.peek();

		while (request != null && warehouse.hasResource(request.getResource())) {
			if (!resource.getResourceType().equals(Resources.FRESH_DOUGH)
					&& !resource.getResourceType().equals(Resources.RESTED_DOUGH)) {
				warehouse.takeResource(request.getResource());
			}
			myAgent.addBehaviour(new InformRequester(resourceQueue.poll()));
			request = resourceQueue.peek();
		}
	}

	private class InformRequester extends OneShotBehaviour {

		private static final long serialVersionUID = 8044766592392978206L;

		private ResourceRequest request;

		public InformRequester(ResourceRequest request) {
			this.request = request;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol(Protocols.RESOURCE);
			msg.setConversationId(request.getRequestId());
			msg.addReceiver(request.getRequester());
			msg.setContent(request.getResource().toJSONObject().toString());

			String output = "Informing: " + request.getRequester().getLocalName() + " "
					+ request.getResource().getResourceType() + " " + request.getResource().getProductId() + " "
					+ request.getResource().getAmount() + " id = " + request.getRequestId();
			Logger.getJADELogger(this.getClass().getName()).log(Logger.INFO, output);
			myAgent.send(msg);
		}

	}

	private class ResourceRequest implements Serializable {

		private static final long serialVersionUID = 166047585913740613L;

		private AID requester;
		private String requestId;
		private Resource resource;

		public ResourceRequest(AID requester, String requestId, Resource resource) {
			this.requester = requester;
			this.requestId = requestId;
			this.resource = resource;
		}

		public AID getRequester() {
			return requester;
		}

		public String getRequestId() {
			return requestId;
		}

		public Resource getResource() {
			return resource;
		}

	}

}

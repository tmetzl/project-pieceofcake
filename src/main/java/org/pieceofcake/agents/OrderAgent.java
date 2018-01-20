package org.pieceofcake.agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.behaviours.FindAgents;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.Bakery;
import org.pieceofcake.objects.Bid;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.Proposal;
import org.pieceofcake.tasks.Task;
import org.pieceofcake.utils.BidCompletionTimeComparator;
import org.pieceofcake.utils.OrderDueDateComparator;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

@SuppressWarnings("serial")
public class OrderAgent extends SynchronizedAgent {

	private transient Bakery myBakery;

	public OrderAgent(Bakery bakery) {
		this.myBakery = bakery;
		this.location = bakery.getLocation();
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		String welcomeMessage = String.format("Bakery %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		// Register the bakery service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.ORDER);
		sd.setName(Services.ORDER_NAME);
		sd.addProtocols(Protocols.ORDER);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}

		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		addBehaviour(seq);
		addBehaviour(new OrderService());
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

	private class OrderService extends CyclicBehaviour {

		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(Protocols.ORDER);
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.CFP) {
					// Customer wants an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(new JSONObject(jsonOrder));

					// Get the price of the order
					Double price = myBakery.getPrice(order);
					ACLMessage reply = msg.createReply();
					if (price != null) {
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price));
					} else {
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
					}

					myAgent.send(reply);
				} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					// Customer accepted an offer

					String jsonOrder = msg.getContent();
					Order order = new Order(new JSONObject(jsonOrder));
					// Add to active orders
					myBakery.addOrder(order);
				}
			} else {
				block();
			}
		}

	}

	private class AdvertiseTasks extends SequentialBehaviour {

		private List<AID> agentList;
		private String protocol;
		private String serviceType;
		private String serviceName;
		private Task task;
		private List<Bid> bestTaskOffered;
		private int numOfReplies = 0;
		private Map<AID, Proposal> proposals;

		public AdvertiseTasks(Task task, String serviceType, String serviceName, List<Bid> bestTaskOffered,
				String protocol) {
			this.task = task;
			this.protocol = protocol;
			this.serviceType = serviceType;
			this.serviceName = serviceName;
			this.bestTaskOffered = bestTaskOffered;
			agentList = new LinkedList<AID>();
			proposals = new HashMap<>();
			// bestTaskOffered = new HashMap<>();

			this.addSubBehaviour(new FindAgents(serviceType, serviceName, agentList));
			this.addSubBehaviour(new SendTasks());
			this.addSubBehaviour(new ReceiveOffers(proposals));
			this.addSubBehaviour(new ProcessProposals());

		}

		private class SendTasks extends OneShotBehaviour {

			@Override
			public void action() {
				// Date currentDate = getScenarioClock().getDate();
				// String output = String.format("%nDay %d %02d:%02d:%02d%n%s",
				// currentDate.getDay(),
				// currentDate.getHour(), currentDate.getMinute(),
				// currentDate.getSecond(), task);
				// logger.log(Logger.INFO, output);

				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				// Add all found agents for this task
				for (int i = 0; i < agentList.size(); i++) {
					msg.addReceiver(agentList.get(i));
				}
				msg.setLanguage("English");
				msg.setOntology("Bakery-order-ontology");
				msg.setProtocol(protocol);
				String content = task.toJSONObject().toString();
				msg.setContent(content);
				myAgent.send(msg);
			}
		}

		private class ReceiveOffers extends Behaviour {

			private boolean allOffersReceived = false;

			public ReceiveOffers(Map<AID, Proposal> proposals) {

			}

			@Override
			public void action() {
				MessageTemplate msgTemplate = MessageTemplate.MatchProtocol(protocol);
				ACLMessage offer = myAgent.receive(msgTemplate);
				if (offer != null) {

					JSONObject jsonProposal = new JSONObject(offer.getContent());
					Proposal proposal = new Proposal();
					proposal.fromJSONObject(jsonProposal);
					proposals.put(offer.getSender(), proposal);
					numOfReplies++;
					if (numOfReplies >= agentList.size()) {
						allOffersReceived = true;
					}
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return allOffersReceived;
			}

		}

		private class ProcessProposals extends OneShotBehaviour {

			private int numOfItems;
			private List<Bid> bids;

			public ProcessProposals() {
				numOfItems = task.getNumOfItems();
			}

			@Override
			public void action() {
				for (AID agentId : agentList) {
					for (int i = 0; i < numOfItems; i++) {
						Bid bid = new Bid();
						bid.setAgentId(agentId);
						bid.setCompletionTime(proposals.get(agentId).getCompletionTimes().get(i));
						bids.add(bid);
					}
				}
				Collections.sort(bids, new BidCompletionTimeComparator());
				for (int i = 0; i < numOfItems; i++) {
					bestTaskOffered.add(bids.get(i));
				}
			}
		}
	}
}

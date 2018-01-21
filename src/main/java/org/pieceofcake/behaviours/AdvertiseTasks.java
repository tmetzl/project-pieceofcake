package org.pieceofcake.behaviours;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.objects.Bid;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Proposal;
import org.pieceofcake.tasks.Task;
import org.pieceofcake.utils.BidCompletionTimeComparator;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AdvertiseTasks extends SequentialBehaviour {

	private static final long serialVersionUID = -2502873150381305836L;

	private List<AID> agentList;
	private String protocol;
	private Task task;
	private Map<AID, List<Date>> bestTaskOffered;
	private int numOfReplies = 0;
	private Map<AID, Proposal> proposals;
	private Map<AID, Task> schedule;

	public AdvertiseTasks(Task task, String serviceType, String serviceName, String protocol) {
		this.task = task;
		this.protocol = protocol;
		this.bestTaskOffered = new HashMap<>();
		agentList = new LinkedList<AID>();
		proposals = new HashMap<>();
		schedule = new HashMap<>();

		this.addSubBehaviour(new FindAgents(serviceType, serviceName, agentList));
		this.addSubBehaviour(new SendTasks());
		this.addSubBehaviour(new ReceiveOffers());
		this.addSubBehaviour(new ProcessProposals());
		this.addSubBehaviour(new ExtractSchedule());

	}

	private class SendTasks extends OneShotBehaviour {

		private static final long serialVersionUID = 4645751581015014285L;

		@Override
		public void action() {
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

		private static final long serialVersionUID = 8793816048979655601L;

		private boolean allOffersReceived = false;

		public ReceiveOffers() {

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

		private static final long serialVersionUID = 4623082428714378802L;

		private int numOfItems;
		private List<Bid> bids;
		private List<Bid> bestOffers;

		public ProcessProposals() {
			numOfItems = task.getNumOfItems();
		}

		@Override
		public void action() {
			for (Map.Entry<AID, Proposal> entry : proposals.entrySet()) {
				for (Date completionTime : entry.getValue().getCompletionTimes()) {
					Bid bid = new Bid();
					bid.setAgentId(entry.getKey());
					bid.setCompletionTime(completionTime);
				}
			}

			Collections.sort(bids, new BidCompletionTimeComparator());
			if (bids.size() >= numOfItems) {
				for (int i = 0; i < numOfItems; i++) {
					bestOffers.add(bids.get(i));
					// bestTaskOffered.put(bids.get(i).getAgentId(),
					// bids.get(i).getCompletionTime());
				}
				for (Bid bid : bestOffers) {
					if (bestTaskOffered.containsKey(bid.getAgentId())) {
						bestTaskOffered.get(bid.getAgentId()).add(bid.getCompletionTime());
					} else {
						// fix this
						bestTaskOffered.put(bid.getAgentId(), (List<Date>) bid.getCompletionTime());

					}
				}
			}
		}
	}

	private class ExtractSchedule extends OneShotBehaviour {

		private static final long serialVersionUID = 5088880955205523064L;

		@Override
		public void action() {

			for (Map.Entry<AID, List<Date>> entry : bestTaskOffered.entrySet()) {
				Collections.sort(entry.getValue());
				int lastIndex = entry.getValue().size() - 1;
				Task partialTask = task.copy();
				partialTask.setNumOfItems(lastIndex + 1);
				partialTask.setDueDate(entry.getValue().get(lastIndex));
				schedule.put(entry.getKey(), partialTask);
			}
		}
	}
}

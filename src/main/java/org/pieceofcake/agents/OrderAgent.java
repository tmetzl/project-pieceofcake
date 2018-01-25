package org.pieceofcake.agents;

import org.json.JSONObject;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.ScheduleOrder;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.OrderContract;

import jade.core.behaviours.CyclicBehaviour;
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

	private CookBook cookBook;
	private String bakeryName;

	public OrderAgent(Location location, String bakeryName, CookBook cookBook) {
		this.cookBook = cookBook;
		this.bakeryName = bakeryName;
		this.location = location;
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
					Double price = cookBook.getSalesPrice(order);
					ACLMessage reply = msg.createReply();
					if (price != null) {
						OrderContract contract = new OrderContract(order, msg.getSender());
						addBehaviour(new ScheduleOrder(contract, cookBook, bakeryName));
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
					//myBakery.addOrder(order);
				}
			} else {
				block();
			}
		}

	}
}

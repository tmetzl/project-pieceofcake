package org.pieceofcake.agents;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.pieceofcake.behaviours.ReceiveStartingTime;
import org.pieceofcake.behaviours.SynchronizeClock;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.interfaces.JobExecutor;
import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.interfaces.Wakeable;
import org.pieceofcake.objects.AlarmService;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.Proposal;
import org.pieceofcake.tasks.Task;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class ProductionAgent<T extends Task> extends SynchronizedAgent implements Wakeable {

	private static final long serialVersionUID = 5250578907085108420L;

	private AlarmService alarmService;
	private Machine<T> machine;
	private JobExecutor jobExecutor;

	public ProductionAgent(Location location, Machine<T> machine) {
		this.location = location;
		this.machine = machine;
		this.alarmService = new AlarmService(getScenarioClock(), this);
		this.jobExecutor = machine.getJobHandler();
	}

	@Override
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(machine.getServiceType());
		sd.setName(machine.getBakeryName());
		sd.addProtocols(machine.getProtocol());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Logger.WARNING, fe.getMessage(), fe);
		}

		String welcomeMessage = String.format("ProductionAgent %s at location (%.2f,%.2f) is ready!",
				getAID().getLocalName(), getLocation().getX(), getLocation().getY());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		seq.addSubBehaviour(new TaskRequestHandler());
		addBehaviour(jobExecutor.getBehaviour());
		addBehaviour(seq);
	}

	@Override
	public Agent getAgent() {
		return this;
	}

	@Override
	public synchronized void wake() {
		Schedule<T> schedule = machine.getScheduleOfDay(getScenarioClock().getDate().getDay());
		Job<T> job = schedule.getNextScheduledJob();
		if (job != null && job.getStart().compareTo(getScenarioClock().getDate()) <= 0) {
			schedule.removeFirst();
			jobExecutor.addSubBehaviour(machine.getJobProcessor(job));
			if (!jobExecutor.isRunning()) {
				addBehaviour(jobExecutor.getBehaviour());
			}
		}

		job = schedule.getNextScheduledJob();
		if (job != null) {
			alarmService.addAlarm(job.getStart());
		}
	}

	public void notifyTaskAdded(T task) {
		int day = task.getDueDate().getDay();
		Schedule<T> schedule = machine.getScheduleOfDay(day);
		Job<T> earliestJob = schedule.getNextScheduledJob();
		if (earliestJob != null) {
			alarmService.addAlarm(earliestJob.getStart());
		}
	}

	private class TaskRequestHandler extends CyclicBehaviour {

		private static final long serialVersionUID = -6432101808595715495L;

		public Proposal getProposal(T task) {
			Proposal proposal = new Proposal();
			proposal.setOrderId(task.getOrderId());
			proposal.setProductId(task.getProductId());

			int day = task.getDueDate().getDay();
			Schedule<T> schedule = machine.getScheduleOfDay(day);
			List<Date> dates = new LinkedList<>();
			Date completionTime = schedule.getEarliestCompletionTime(task);
			dates.add(completionTime);
			proposal.setCompletionTimes(dates);
			return proposal;
		}

		@Override
		public void action() {
			MessageTemplate msgTemplate = MessageTemplate.or(MessageTemplate.MatchProtocol(machine.getProtocol()),
					MessageTemplate.MatchProtocol(Protocols.CANCEL_ORDER));
			ACLMessage msg = myAgent.receive(msgTemplate);
			if (msg != null) {
				if (msg.getProtocol().equals(machine.getProtocol())) {
					T task = machine.getTask(msg.getContent());
					if (msg.getPerformative() == ACLMessage.CFP) {
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(getProposal(task).toJSONObject().toString());
						myAgent.send(reply);
					} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						int day = task.getDueDate().getDay();
						Schedule<T> schedule = machine.getScheduleOfDay(day);
						schedule.insert(task);
						notifyTaskAdded(task);
					}
				} else {
					Order order = new Order(new JSONObject(msg.getContent()));
					int day = order.getDueDate().getDay();
					Schedule<T> schedule = machine.getScheduleOfDay(day);
					schedule.removeTasksFromOrder(order.getGuiId());
				}
			} else {
				block();
			}
		}

	}

}

package org.pieceofcake.behaviours;

import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.config.Services;
import org.pieceofcake.tasks.Task;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class NotifyTaskCompleted<T extends Task> extends SequentialBehaviour {

	private static final long serialVersionUID = -613337243988989195L;

	private List<AID> agentIds;
	private String protocol;
	private T task;

	public NotifyTaskCompleted(String protocol, String bakeryName, T task) {
		this.agentIds = new LinkedList<>();
		this.protocol = protocol;
		this.task = task;

		this.addSubBehaviour(new FindAgents(Services.TASK_INFO, bakeryName, agentIds));
		this.addSubBehaviour(new SendMessage());
	}

	private class SendMessage extends OneShotBehaviour {

		private static final long serialVersionUID = 5351904296017798530L;

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(agentIds.get(0));
			msg.setProtocol(protocol);
			msg.setContent(task.toJSONObject().toString());
			myAgent.send(msg);

		}

	}

}

package org.pieceofcake.objects;

import jade.core.AID;

public class Bid {

	private Date completionTime;
	private AID agentId;

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTimes) {
		this.completionTime = completionTimes;
	}

	public AID getAgentId() {
		return agentId;
	}

	public void setAgentId(AID agentId) {
		this.agentId = agentId;
	}

}

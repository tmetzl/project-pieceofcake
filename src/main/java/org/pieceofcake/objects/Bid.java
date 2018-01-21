package org.pieceofcake.objects;

import java.io.Serializable;

import jade.core.AID;

public class Bid implements Serializable {

	private static final long serialVersionUID = 8217916051757483752L;
	
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

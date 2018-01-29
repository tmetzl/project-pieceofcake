package org.pieceofcake.objects;

import java.io.Serializable;

import jade.core.AID;

public class CustomerContract implements Serializable {

	private static final long serialVersionUID = -1924280612446059866L;
	
	private Order order;
	private AID bakeryId;
	private boolean completed;
	
	public CustomerContract(Order order, AID bakeryId) {
		this.order = order;
		this.bakeryId = bakeryId;
		this.completed = false;
	}
	
	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted() {
		this.completed = true;
	}

	public Order getOrder() {
		return order;
	}

	public AID getBakeryId() {
		return bakeryId;
	}

}

package org.pieceofcake.objects;

import java.io.Serializable;

import org.pieceofcake.tasks.Task;

public class ContractTask<T extends Task> implements Serializable {

	private static final long serialVersionUID = -2292422955180820023L;

	private T task;
	private boolean completed;

	public ContractTask(T task) {
		this.task = task;
		this.completed = false;
	}

	public T getTask() {
		return task;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted() {
		this.completed = true;
	}

}
package org.pieceofcake.machines;

import java.util.concurrent.Semaphore;

import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.tasks.Task;

import jade.util.Logger;

public abstract class SingleMachine<T extends Task> implements Machine<T> {

	private static final long serialVersionUID = -5488123242981992996L;
	
	private Semaphore semaphore;
	
	public SingleMachine() {
		this.semaphore = new Semaphore(1, true);
	}

	@Override
	public void aquireMachine() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Logger.getJADELogger(this.getClass().getName()).log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void releaseMachine() {
		semaphore.release();
	}

}

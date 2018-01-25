package org.pieceofcake.machines;

import java.util.concurrent.Semaphore;

import org.pieceofcake.tasks.Task;

import jade.util.Logger;

public abstract class SingleMachine<T extends Task> extends AbstractMachine<T> {

	private static final long serialVersionUID = -5488123242981992996L;
	
	private Semaphore semaphore;
	
	public SingleMachine(String bakeryName, String serviceType, String protocol) {
		super(bakeryName, serviceType, protocol);
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
	public boolean tryAquireMachine() {
		return semaphore.tryAcquire();
	}

	@Override
	public void releaseMachine() {
		semaphore.release();
	}

}

package org.pieceofcake.behaviours;

import org.pieceofcake.interfaces.JobExecutor;

import jade.core.behaviours.CompositeBehaviour;
import jade.core.behaviours.ParallelBehaviour;

public class ParallelJobExecutor extends ParallelBehaviour implements JobExecutor {
	
	private static final long serialVersionUID = -3259578530668371325L;
	
	private boolean isRunning = false;
	
	@Override
	public void onStart() {
		isRunning = true;
	}
	
	@Override
	public int onEnd() {
		isRunning = false;
		return 0;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public CompositeBehaviour getBehaviour() {
		return this;
	}

}

package org.pieceofcake.behaviours;

import org.pieceofcake.interfaces.JobExecutor;

import jade.core.behaviours.CompositeBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class SequentialJobExecutor extends SequentialBehaviour implements JobExecutor {
	
	private static final long serialVersionUID = 8559305910749595788L;
	
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

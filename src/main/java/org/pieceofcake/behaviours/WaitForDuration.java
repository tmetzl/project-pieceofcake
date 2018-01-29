package org.pieceofcake.behaviours;

import org.pieceofcake.objects.ScenarioClock;

import jade.core.behaviours.Behaviour;

public class WaitForDuration extends Behaviour {

	private static final long serialVersionUID = 3282372602816514252L;
	
	private long duration;
	private long end;
	private boolean waitingFinished;

	
	public WaitForDuration(long duration) {
		this.duration = ScenarioClock.millisFromScenarioSeconds(duration);
	}
	
	@Override
	public void onStart() {
		end = System.currentTimeMillis() + duration;
	}

	@Override
	public void action() {
		long remainingMillis = end - System.currentTimeMillis();
		if (remainingMillis <= 0) {
			waitingFinished = true;
		} else {
			block(remainingMillis);
		}
		
	}

	@Override
	public boolean done() {
		return waitingFinished;
	}

}

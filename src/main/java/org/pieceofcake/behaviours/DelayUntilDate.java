package org.pieceofcake.behaviours;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.ScenarioClock;

import jade.core.behaviours.Behaviour;

public class DelayUntilDate extends Behaviour {

	private static final long serialVersionUID = -5862720298889200813L;
	
	private ScenarioClock clock;
	private Date date;
	private boolean waitingFinished;
	
	public DelayUntilDate(ScenarioClock clock, Date date) {
		this.clock = clock;
		this.date = date;
	}

	@Override
	public void action() {
		long remainingScenarioSeconds = date.toSeconds() - clock.getDate().toSeconds();
		long remainingMillis = ScenarioClock.millisFromScenarioSeconds(remainingScenarioSeconds);
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

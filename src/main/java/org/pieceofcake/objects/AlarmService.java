package org.pieceofcake.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.pieceofcake.behaviours.DelayUntilDate;
import org.pieceofcake.interfaces.Wakeable;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class AlarmService implements Serializable {

	private static final long serialVersionUID = -5756510772319790949L;
	
	private ScenarioClock clock;
	private Wakeable wakeable;
	private Map<Date, WaitAndNotify> alarms;

	public AlarmService(ScenarioClock clock, Wakeable wakeable) {
		this.clock = clock;
		this.wakeable = wakeable;
		this.alarms = new HashMap<>();
	}

	public void addAlarm(Date date) {
		WaitAndNotify waitAndNotfiy = new WaitAndNotify(date);
		alarms.putIfAbsent(date, waitAndNotfiy);
		wakeable.getAgent().addBehaviour(waitAndNotfiy);
	}

	public void removeAlarm(Date date) {
		WaitAndNotify behaviour = alarms.get(date);
		if (behaviour != null) {
			wakeable.getAgent().removeBehaviour(behaviour);
		}
	}

	public void clear() {
		for (Date date : alarms.keySet()) {
			removeAlarm(date);
		}
		alarms.clear();
	}

	private class WaitAndNotify extends SequentialBehaviour {

		private static final long serialVersionUID = 6342210661238178219L;
		
		private Date date;

		public WaitAndNotify(Date date) {
			this.date = date;
			this.addSubBehaviour(new DelayUntilDate(clock, date));
			this.addSubBehaviour(new Notify());
		}

		private class Notify extends OneShotBehaviour {

			private static final long serialVersionUID = 290883473959726568L;

			@Override
			public void action() {
				wakeable.wake();
				removeAlarm(date);
			}

		}

	}

}

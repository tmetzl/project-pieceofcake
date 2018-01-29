package org.pieceofcake.objects;

import java.io.Serializable;

public class ScenarioClock implements Serializable {

	private static final long serialVersionUID = 4923509163246771424L;
	public static final long SECONDS_PER_SCENARIO_DAY = 60;

	private long startingTime;
	private long timeOffset;

	public ScenarioClock() {
		this.startingTime = Long.MAX_VALUE;
	}

	public void setStartingTime(long startingTime) {
		this.startingTime = startingTime;
	}

	public void setTimeOffset(long timeOffset) {
		this.timeOffset += timeOffset;
	}

	public long getSynchronizedTime() {
		return System.currentTimeMillis() + timeOffset;
	}

	public Date getDate() {
		long millisSinceStartUp = Math.max(0, getSynchronizedTime() - startingTime);
		long time = millisSinceStartUp * 86400l / (SECONDS_PER_SCENARIO_DAY * 1000l);
		int second = (int) (time % 60);
		time /= 60;
		int minute = (int) (time % 60);
		time /= 60;
		int hour = (int) (time % 24);
		time /= 24;
		int day = (int) time;
		return new Date(day, hour, minute, second);
	}

	public static long millisFromScenarioSeconds(long scenarioSeconds) {
		return (scenarioSeconds * SECONDS_PER_SCENARIO_DAY * 1000l) / 86400;
	}

}

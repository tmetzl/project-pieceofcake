package maas.objects;

import java.io.Serializable;

public class ScenarioClock implements Serializable {

	private static final long serialVersionUID = 4923509163246771424L;
	public static final long SECONDS_PER_SCENARIO_DAY = 24;

	private boolean started;
	private long startingTime;
	private long timeOffset;

	public ScenarioClock() {
		this.started = false;
	}
	
	public void start() {
		if (!started) {
			startingTime = getSynchronizedTime();
			started = true;
		}
	}
	
	public void setTimeOffset(long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	public long getSynchronizedTime() {
		return System.currentTimeMillis() + timeOffset;
	}

	public Date getDate() {
		if (started) {
			long secondsSinceStartUp = (getSynchronizedTime() - startingTime) / 1000l;
			long time = secondsSinceStartUp * 24*60*60 / SECONDS_PER_SCENARIO_DAY;
			int second = (int) (time % 60);
			time /= 60;
			int minute = (int) (time % 60);
			time /= 60;
			int hour = (int) (time % 24);
			time /= 24;
			int day = (int) time;
			return new Date(day, hour, minute, second);
		} else {
			return new Date(0, 0, 0, 0);
		}
	}
	
	public static long millisFromScenarioSeconds(long scenarioSeconds) {
		double factor = SECONDS_PER_SCENARIO_DAY / 86400d;
		double millis = scenarioSeconds * factor;
		return (long) millis;
	}

}

package maas.objects;

import java.io.Serializable;

public class ScenarioClock implements Serializable {

	private static final long serialVersionUID = 4923509163246771424L;
	public static final long SECONDS_PER_SCENARIO_DAY = 6;

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
		this.timeOffset += timeOffset;
	}
	
	public long getSynchronizedTime() {
		return System.currentTimeMillis() + timeOffset;
	}

	public Date getDate1() {
		if (started) {
			long millisSinceStartUp = (getSynchronizedTime() - startingTime);
			long time1 = millisSinceStartUp * 86400l / SECONDS_PER_SCENARIO_DAY / 1000l;
			long secondsSinceStartUp = (getSynchronizedTime() - startingTime) / 1000l;
			long time = secondsSinceStartUp * 86400l / SECONDS_PER_SCENARIO_DAY;
			System.out.println(time+"\t"+time1);
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
	
	public Date getDate() {
		if (started) {
			long millisSinceStartUp = (getSynchronizedTime() - startingTime);
			//System.out.println(millisSinceStartUp);
			long time = millisSinceStartUp * 86400l / (SECONDS_PER_SCENARIO_DAY * 1000l);
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
		long millis = (scenarioSeconds * SECONDS_PER_SCENARIO_DAY * 1000l) / 86400;
		return millis;
	}
	
	public static void main(String[] args) {
		ScenarioClock clock = new ScenarioClock();
		clock.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(clock.getDate());
		System.out.println(clock.getDate1());
	}

}

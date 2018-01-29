package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScenarioClockTest {

	@Test
	public void testGetDate() throws InterruptedException {
		ScenarioClock clock = new ScenarioClock();
		assertEquals(new Date(0, 0, 0, 0), clock.getDate());
		
		// Set starting time to one second in the past
		clock.setStartingTime(System.currentTimeMillis() - 1000);
		Date dateAfterOneSecond = clock.getDate();
		long expectedHours = 24 / ScenarioClock.SECONDS_PER_SCENARIO_DAY;
		assertEquals(expectedHours, dateAfterOneSecond.getHour());
	}

	@Test
	public void testMillisFromScenarioSeconds() {
		long secondsInADay = 24 * 60 * 60;
		long millisPerScenarioDay = ScenarioClock.SECONDS_PER_SCENARIO_DAY * 1000l;
		assertEquals(millisPerScenarioDay, ScenarioClock.millisFromScenarioSeconds(secondsInADay));
	}

	@Test
	public void testTimeOffset() {
		ScenarioClock clock = new ScenarioClock();
		clock.setTimeOffset(1000l);
		long measuredOffset = clock.getSynchronizedTime() - System.currentTimeMillis();
		assertTrue(950l < measuredOffset);
		assertTrue(1050 > measuredOffset);
	}

}

package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Test;

public class DateTest {

	@Test
	public void testGetters() {
		Date date = new Date(1, 2, 3, 4);
		assertEquals(1, date.getDay());
		assertEquals(2, date.getHour());
		assertEquals(3, date.getMinute());
		assertEquals(4, date.getSecond());
	}

	@Test
	public void testEquals() {
		Date date = new Date(4, 3, 2, 1);
		Object object = new Object();
		assertNotEquals(date, object);

		Date anotherDate = new Date(1, 2, 3, 4);
		assertNotEquals(date, anotherDate);

		Date sameDate = new Date(4, 3, 2, 1);
		assertEquals(date, sameDate);
	}

	@Test
	public void testHashCode() {
		Date Date = new Date(8, 5, 2, 0);
		Date sameDate = new Date(8, 5, 2, 0);
		assertEquals(Date, sameDate);
		assertEquals(Date.hashCode(), sameDate.hashCode());
	}

	@Test
	public void testJSONMethods() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("day", 7);
		jsonObject.put("hour", 5);
		jsonObject.put("minute", 3);
		jsonObject.put("second", 0);
		Date date = new Date();
		date.fromJSONObject(jsonObject);
		assertEquals(7, date.getDay());
		assertEquals(5, date.getHour());
		assertEquals(3, date.getMinute());
		assertEquals(0, date.getSecond());

		JSONObject jsonObjectFromDate = date.toJSONObject();
		Date anotherDate = new Date();
		anotherDate.fromJSONObject(jsonObjectFromDate);
		assertEquals(date, anotherDate);
	}
	
	@Test
	public void testToString() {
		String dateString = "01:02:03:04";
		Date date = new Date(1, 2, 3, 4);
		assertEquals(dateString, date.toString());
	}
	
	@Test
	public void testToSeconds() {
		Date date = new Date(1, 2, 3, 4);
		assertEquals(93784l, date.toSeconds());
		Date otherDate = new Date(93784l);
		assertEquals(date, otherDate);
	}
	
	@Test
	public void testCompareTo() {
		Date earlyDate = new Date(0, 1, 2, 3);
		Date lateDate = new Date(17, 2, 18, 5);
		assertEquals(-1, earlyDate.compareTo(lateDate));
		assertEquals(1, lateDate.compareTo(earlyDate));
		assertEquals(0, earlyDate.compareTo(earlyDate));
	}
}

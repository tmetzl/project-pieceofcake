package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.pieceofcake.objects.Location;

public class LocationTest {

	@Test
	public void testGetters() {
		Location location = new Location(5.3, 7.8);
		assertEquals(5.3, location.getX(), 1e-10);
		assertEquals(7.8, location.getY(), 1e-10);
	}

	@Test
	public void testEquals() {
		Location location = new Location(2.5, 8.7);
		Object object = new Object();
		assertNotEquals(location, object);

		Location anotherLocation = new Location(2.3, 6.5);
		assertNotEquals(location, anotherLocation);

		Location sameLocation = new Location(2.5, 8.7);
		assertEquals(location, sameLocation);
	}

	@Test
	public void testHashCode() {
		Location location = new Location(1.5, 5.4);
		Location sameLocation = new Location(1.5, 5.4);
		assertEquals(location, sameLocation);
		assertEquals(location.hashCode(), sameLocation.hashCode());
	}
	
	@Test
	public void testJSONMethods() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("x", 2.5);
		jsonObject.put("y", 3.6);
		
		Location location = new Location();
		location.fromJSONObject(jsonObject);
		assertEquals(2.5, location.getX(), 1e-10);
		assertEquals(3.6, location.getY(), 1e-10);
		
		JSONObject jsonObjectFromLocation = location.toJSONObject();
		Location anotherLocation = new Location();
		anotherLocation.fromJSONObject(jsonObjectFromLocation);
		assertEquals(location, anotherLocation);
	}

}

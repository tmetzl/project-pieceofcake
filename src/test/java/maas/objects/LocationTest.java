package maas.objects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class LocationTest {
	
	@Test
	public void testLocation() {
		Location location = new Location(5.3, 7.8);
		assertEquals(5.3, location.getX(), 1e-10);
		assertEquals(7.8, location.getY(), 1e-10);
	}

}

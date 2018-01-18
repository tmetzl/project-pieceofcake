package org.pieceofcake.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class ServiceTest {
	
	@Test
	public void testPrivateConstructorAccessibility() throws Exception {
		Constructor<Services> constructor = Services.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		
		constructor.setAccessible(true);
		constructor.newInstance();	
	}

}

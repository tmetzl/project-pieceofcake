package org.pieceofcake.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class ResourcesTest {
	
	@Test
	public void testPrivateConstructorAccessibility() throws Exception {
		Constructor<Resources> constructor = Resources.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		
		constructor.setAccessible(true);
		constructor.newInstance();	
	}

}

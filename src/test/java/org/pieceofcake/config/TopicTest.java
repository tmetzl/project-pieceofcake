package org.pieceofcake.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.pieceofcake.config.Topic;

public class TopicTest {
	
	@Test
	public void testPrivateConstructorAccessibility() throws Exception {
		Constructor<Topic> constructor = Topic.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		
		constructor.setAccessible(true);
		constructor.newInstance();		
	}

}

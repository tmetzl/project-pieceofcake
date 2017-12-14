package maas.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class TopicTest {
	
	@Test
	public void testPrivateConstructorAccessibility() {
		final Constructor<?>[] constructors = Topic.class.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		}
	}

}

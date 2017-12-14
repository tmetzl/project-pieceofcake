package maas.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class ProtocolTest {
	
	@Test
	public void testPrivateConstructorAccessibility() {
		final Constructor<?>[] constructors = Protocols.class.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		}
	}

}

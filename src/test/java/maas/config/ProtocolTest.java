package maas.config;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class ProtocolTest {
	
	@Test
	public void testPrivateConstructorAccessibility() throws Exception {
		Constructor<Protocols> constructor = Protocols.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		
		constructor.setAccessible(true);
		constructor.newInstance();	
	}

}

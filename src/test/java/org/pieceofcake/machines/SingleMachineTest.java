package org.pieceofcake.machines;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingleMachineTest {
	
	@Test
	public void testAccess() {
		KneadingMachine machine = new KneadingMachine("test-bakery");
		
		machine.aquireMachine();
		
		assertFalse(machine.tryAquireMachine());
		
		machine.releaseMachine();
		
		assertTrue(machine.tryAquireMachine());
		
		machine.releaseMachine();
	}

}

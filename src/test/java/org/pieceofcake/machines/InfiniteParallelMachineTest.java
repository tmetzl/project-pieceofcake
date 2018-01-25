package org.pieceofcake.machines;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InfiniteParallelMachineTest {
	
	@Test
	public void testAccess() {
		RestingMachine machine = new RestingMachine("test-bakery");
		
		machine.aquireMachine();
		
		assertTrue(machine.tryAquireMachine());
		
		machine.releaseMachine();
		
		assertTrue(machine.tryAquireMachine());
		
		machine.releaseMachine();
	}

}

package org.pieceofcake.machines;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jade.core.behaviours.ParallelBehaviour;

public class InfiniteParallelMachineTest {
	
	@Test
	public void testHandler() {
		RestingMachine machine = new RestingMachine("test-bakery");
		
		assertTrue(machine.getJobHandler() instanceof ParallelBehaviour);
	}

}

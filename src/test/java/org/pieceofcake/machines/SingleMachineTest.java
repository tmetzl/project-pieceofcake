package org.pieceofcake.machines;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jade.core.behaviours.SequentialBehaviour;

public class SingleMachineTest {
	
	@Test
	public void testHandler() {
		KneadingMachine machine = new KneadingMachine("test-bakery");
		
		assertTrue(machine.getJobHandler() instanceof SequentialBehaviour);
	}

}

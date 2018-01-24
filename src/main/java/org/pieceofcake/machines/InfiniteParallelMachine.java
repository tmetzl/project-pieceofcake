package org.pieceofcake.machines;

import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.tasks.Task;

public abstract class InfiniteParallelMachine<T extends Task> implements Machine<T> {

	private static final long serialVersionUID = -6011452324107005012L;

	@Override
	public void aquireMachine() {
		
	}
	
	@Override
	public void releaseMachine() {
		
	}

}

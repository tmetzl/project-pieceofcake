package org.pieceofcake.machines;

import org.pieceofcake.behaviours.ParallelJobExecutor;
import org.pieceofcake.interfaces.JobExecutor;
import org.pieceofcake.tasks.Task;

public abstract class InfiniteParallelMachine<T extends Task> extends AbstractMachine<T> {
	
	private JobExecutor handler;

	public InfiniteParallelMachine(String bakeryName, String serviceType, String protocol) {
		super(bakeryName, serviceType, protocol);
		this.handler = new ParallelJobExecutor();
	}

	private static final long serialVersionUID = -6011452324107005012L;

	@Override
	public JobExecutor getJobHandler() {
		return handler;
	}

}

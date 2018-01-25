package org.pieceofcake.machines;

import org.pieceofcake.behaviours.SequentialJobExecutor;
import org.pieceofcake.interfaces.JobExecutor;
import org.pieceofcake.tasks.Task;

public abstract class SingleMachine<T extends Task> extends AbstractMachine<T> {

	private static final long serialVersionUID = -5488123242981992996L;
	
	private SequentialJobExecutor handler;

	public SingleMachine(String bakeryName, String serviceType, String protocol) {
		super(bakeryName, serviceType, protocol);
		this.handler = new SequentialJobExecutor();
	}

	@Override
	public JobExecutor getJobHandler() {
		return handler;
	}

}

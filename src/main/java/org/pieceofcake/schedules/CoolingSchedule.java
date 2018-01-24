package org.pieceofcake.schedules;

import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.CoolingTask;

public class CoolingSchedule extends ProductionSchedule<CoolingTask> {

	private static final long serialVersionUID = -1184299612402449149L;

	@Override
	public long getProductionTime(Job<CoolingTask> prevJob, Job<CoolingTask> nextJob, CoolingTask task) {
		return (long) ((task.getBakingTemperature() - 40)*task.getCoolingTimeFactor());		
	}

	@Override
	public CoolingTask addToJob(Job<CoolingTask> job, CoolingTask task) {
		return null;
	}

	@Override
	public CoolingTask addBetweenJobs(Job<CoolingTask> prevJob, Job<CoolingTask> nextJob, CoolingTask task) {
		return task.copy();
	}

	@Override
	public Job<CoolingTask> getJob(String productId) {
		return null;
	}

}

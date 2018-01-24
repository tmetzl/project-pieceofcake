package org.pieceofcake.schedules;

import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.RestingTask;

public class RestingSchedule extends ProductionSchedule<RestingTask> {

	private static final long serialVersionUID = -2421485093925747166L;

	@Override
	public long getProductionTime(Job<RestingTask> prevJob, Job<RestingTask> nextJob, RestingTask task) {
		return task.getRestingTime();
	}

	@Override
	public RestingTask addToJob(Job<RestingTask> job, RestingTask task) {
		return null;
	}

	@Override
	public RestingTask addBetweenJobs(Job<RestingTask> prevJob, Job<RestingTask> nextJob, RestingTask task) {
		return task.copy();
	}

	@Override
	public Job<RestingTask> getJob(String productId) {
		return null;
	}

}

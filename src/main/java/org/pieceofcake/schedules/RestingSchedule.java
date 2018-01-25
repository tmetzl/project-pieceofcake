package org.pieceofcake.schedules;

import java.util.LinkedList;
import java.util.List;

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
	public List<RestingTask> addBetweenJobs(Job<RestingTask> prevJob, Job<RestingTask> nextJob, RestingTask task) {
		List<RestingTask> tasks = new LinkedList<>();
		tasks.add(task.copy());
		return tasks;
	}

	@Override
	public Job<RestingTask> getJob(String productId) {
		return null;
	}

}

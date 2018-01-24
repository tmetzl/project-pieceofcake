package org.pieceofcake.schedules;

import java.util.LinkedList;
import java.util.List;

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
	public List<CoolingTask> addBetweenJobs(Job<CoolingTask> prevJob, Job<CoolingTask> nextJob, CoolingTask task) {
		List<CoolingTask> tasks = new LinkedList<>();
		tasks.add(task.copy());
		return tasks;
	}

	@Override
	public Job<CoolingTask> getJob(String productId) {
		return null;
	}

}

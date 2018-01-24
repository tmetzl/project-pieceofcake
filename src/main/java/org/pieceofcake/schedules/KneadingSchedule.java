package org.pieceofcake.schedules;

import java.util.List;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.KneadingTask;

public class KneadingSchedule extends ProductionSchedule<KneadingTask> {

	private static final long serialVersionUID = 2248657955736429337L;

	@Override
	public long getProductionTime(Job<KneadingTask> prevJob, Job<KneadingTask> nextJob, KneadingTask task) {
		return task.getKneadingTime();
	}

	@Override
	public KneadingTask addToJob(Job<KneadingTask> job, KneadingTask task) {
		return null;
	}

	@Override
	public KneadingTask addBetweenJobs(Job<KneadingTask> prevJob, Job<KneadingTask> nextJob, KneadingTask task) {
		Date startDate = task.getReleaseDate();
		if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
			startDate = prevJob.getEnd();
		}
		long availableTime = Long.MAX_VALUE;
		if (nextJob != null) {
			availableTime = nextJob.getStart().toSeconds() - startDate.toSeconds();
		}
		if (availableTime >= task.getKneadingTime()) {
			return task.copy();
		}
		return null;
	}

	@Override
	public Job<KneadingTask> getJob(String productId) {
		for (Job<KneadingTask> job : getSchedule()) {
			List<KneadingTask> associatedTasks = job.getAssociatedTasks();
			for (KneadingTask task : associatedTasks) {
				if (task.getProductId().equals(productId)) {
					return job;
				}
			}
		}
		return null;
	}

}

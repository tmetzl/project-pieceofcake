package org.pieceofcake.schedules;

import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.KneadingTask;

public class KneadingSchedule extends OneTimeSchedule<KneadingTask> {

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
	public List<KneadingTask> addBetweenJobs(Job<KneadingTask> prevJob, Job<KneadingTask> nextJob, KneadingTask task) {
		Date startDate = task.getReleaseDate();
		List<KneadingTask> tasks = new LinkedList<>();
		if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
			startDate = prevJob.getEnd();
		}
		long availableTime = Long.MAX_VALUE;
		if (nextJob != null) {
			availableTime = nextJob.getStart().toSeconds() - startDate.toSeconds();
		}
		if (availableTime >= task.getKneadingTime()) {
			tasks.add(task.copy());
		}
		return tasks;
	}

}

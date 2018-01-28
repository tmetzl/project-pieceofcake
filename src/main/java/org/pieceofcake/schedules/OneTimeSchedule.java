package org.pieceofcake.schedules;

import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.Task;

public abstract class OneTimeSchedule<T extends Task> extends ProductionSchedule<T> {
	
	private static final long serialVersionUID = 8365744742801423265L;
	
	private List<Job<T>> executedJobs;
	
	public OneTimeSchedule() {
		executedJobs = new LinkedList<>();
	}
	
	@Override
	public Job<T> getJob(String productId) {
		List<Job<T>> allJobs = new LinkedList<>();
		allJobs.addAll(executedJobs);
		allJobs.addAll(getSchedule());
		for (Job<T> job : allJobs) {
			List<T> associatedTasks = job.getAssociatedTasks();
			for (T task : associatedTasks) {
				if (task.getProductId().equals(productId)) {
					return job;
				}
			}
		}
		return null;
	}
	
	@Override
	public void removeFirst() {
		List<Job<T>> schedule = getSchedule();
		if (!schedule.isEmpty()) {
			executedJobs.add(schedule.remove(0));
		}
	}

}

package org.pieceofcake.schedules;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.Task;
import org.pieceofcake.utils.JobStartDateComparator;

public abstract class ProductionSchedule<T extends Task> implements Schedule<T> {

	private static final long serialVersionUID = -8950490337888527237L;
	
	private List<Job<T>> schedule;

	public ProductionSchedule() {
		this.schedule = new LinkedList<>();
	}

	public abstract long getProductionTime(Job<T> prevJob, Job<T> nextJob, T task);

	public abstract T addToJob(Job<T> job, T task);

	public abstract T addBetweenJobs(Job<T> prevJob, Job<T> nextJob, T task);

	public abstract Job<T> getJob(String productId);
	
	public List<Job<T>> getSchedule() {
		return this.schedule;
	}

	public Job<T> createJobBetween(Job<T> prevJob, Job<T> currentJob, T task) {
		T addableTask = addBetweenJobs(prevJob, currentJob, task);
		if (addableTask != null) {
			int items = addableTask.getNumOfItems();
			task.setNumOfItems(task.getNumOfItems() - items);
			Date startDate = addableTask.getReleaseDate();
			if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
				startDate = prevJob.getEnd();
			}
			Date endDate = new Date(startDate.toSeconds() + getProductionTime(prevJob, currentJob, addableTask));
			return new Job<>(startDate, endDate, addableTask);
		}
		return null;
	}

	public List<Job<T>> createJobs(T task) {
		List<Job<T>> newJobs = new LinkedList<>();

		Job<T> existingJob = getJob(task.getProductId());
		if (existingJob != null) {
			return newJobs;
		}

		T remainingTask = (T) task.copy();
		Job<T> prevJob = null;
		for (int i = 0; i < schedule.size(); i++) {
			Job<T> currentJob = schedule.get(i);
			// Check if and how much we can add between the previous and current
			// job
			Job<T> job = createJobBetween(prevJob, currentJob, remainingTask);
			if (job != null) {
				newJobs.add(job);
			}
			if (remainingTask.getNumOfItems() <= 0) {
				return newJobs;
			}
			// Check if and how much we can add to an existing job
			T addableTask = addToJob(currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				currentJob.addTask(addableTask);
				if (remainingTask.getNumOfItems() <= 0) {
					return newJobs;
				}
			}

			prevJob = currentJob;
		}
		// Schedule remaining task at the end
		Job<T> job = createJobBetween(prevJob, null, remainingTask);
		newJobs.add(job);
		return newJobs;
	}

	@Override
	public Date getEarliestCompletionTime(T task) {
		List<Job<T>> jobs = createJobs(task);
		if (jobs.isEmpty()) {
			return getJob(task.getProductId()).getEnd();
		}
		return jobs.get(jobs.size() - 1).getEnd();
	}

	@Override
	public void insert(T task) {
		schedule.addAll(createJobs(task));
		Collections.sort(schedule, new JobStartDateComparator());
	}

	@Override
	public Job<T> getNextScheduledJob() {
		if (!schedule.isEmpty()) {
			return schedule.get(0);
		}
		return null;
	}

	@Override
	public void removeFirst() {
		if (!schedule.isEmpty()) {
			schedule.remove(0);
		}
	}

	@Override
	public void removeTasksFromOrder(String orderId) {
		Iterator<Job<T>> iter = schedule.iterator();
		while (iter.hasNext()) {
			Job<T> job = iter.next();
			job.removeTasksFromOrder(orderId);
			if (job.getAssociatedTasks().isEmpty()) {
				iter.remove();
			}
		}
	}

}

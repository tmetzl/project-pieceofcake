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

	public abstract List<T> addBetweenJobs(Job<T> prevJob, Job<T> nextJob, T task);

	public abstract Job<T> getJob(String productId);

	public List<Job<T>> getSchedule() {
		return this.schedule;
	}

	public List<Job<T>> createJobBetween(Job<T> prevJob, Job<T> currentJob, T task) {
		List<T> addableTasks = addBetweenJobs(prevJob, currentJob, task);
		if (addableTasks != null) {
			List<Job<T>> jobs = new LinkedList<>();
			for (T addableTask : addableTasks) {
				int items = addableTask.getNumOfItems();
				task.setNumOfItems(task.getNumOfItems() - items);
				Date startDate = addableTask.getReleaseDate();
				if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
					startDate = prevJob.getEnd();
				}
				Date endDate = new Date(startDate.toSeconds() + getProductionTime(prevJob, currentJob, addableTask));
				prevJob = new Job<>(startDate, endDate, addableTask);
				jobs.add(prevJob);
			}
			return jobs;
		}
		return null;
	}

	public List<Job<T>> createJobs(T task, boolean insert) {
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
			List<Job<T>> jobs = createJobBetween(prevJob, currentJob, remainingTask);
			if (jobs != null) {
				newJobs.addAll(jobs);
			}
			if (remainingTask.getNumOfItems() <= 0) {
				return newJobs;
			}
			// Check if and how much we can add to an existing job
			T addableTask = addToJob(currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				if (insert) {
					currentJob.addTask(addableTask);
				} else {
					newJobs.add(currentJob);
				}
				if (remainingTask.getNumOfItems() <= 0) {
					return newJobs;
				}
			}

			prevJob = currentJob;
		}
		// Schedule remaining task at the end
		List<Job<T>> jobs = createJobBetween(prevJob, null, remainingTask);
		newJobs.addAll(jobs);
		return newJobs;
	}

	@Override
	public Date getEarliestCompletionTime(T task) {
		List<Job<T>> jobs = createJobs(task, false);
		if (jobs.isEmpty()) {
			return getJob(task.getProductId()).getEnd();
		}
		return jobs.get(jobs.size() - 1).getEnd();
	}

	@Override
	public void insert(T task) {
		schedule.addAll(createJobs(task, true));
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

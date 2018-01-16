package org.pieceofcake.schedules;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.Task;

public abstract class ProductionSchedule<T extends Task> implements Schedule<T> {

	protected List<Job<T>> schedule;

	public ProductionSchedule() {
		this.schedule = new LinkedList<>();
	}

	public abstract long getProductionTime(Job<T> prevJob, Job<T> nextJob, T task);

	public abstract T addToJob(Job<T> job, T task);

	public abstract T addBetweenJobs(Job<T> prevJob, Job<T> nextJob, T task);

	public Job<T> getJob(String productId) {
		return null;
	}

	@Override
	public Date getEarliestCompletionTime(T task) {
		if (schedule.isEmpty()) {
			return new Date(task.getReleaseDate().toSeconds() + getProductionTime(null, null, task));
		}

		Job<T> existingJob = getJob(task.getProductId());
		if (existingJob != null) {
			return existingJob.getEnd();
		}

		T remainingTask = (T) task.copy();
		Job<T> prevJob = null;
		for (int i = 0; i < schedule.size(); i++) {
			Job<T> currentJob = schedule.get(i);
			// Check if and how much we can add between the previous and current
			// job
			T addableTask = addBetweenJobs(prevJob, currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				if (remainingTask.getNumOfItems() <= 0) {
					Date startDate = task.getReleaseDate();
					if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
						startDate = prevJob.getEnd();
					}
					return new Date(startDate.toSeconds() + getProductionTime(prevJob, currentJob, addableTask));
				}
			}
			// Check if and how much we can add to an existing job
			addableTask = addToJob(currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				if (remainingTask.getNumOfItems() <= 0) {
					return currentJob.getEnd();
				}
			}

			prevJob = currentJob;
		}
		// Schedule remaining task at the end
		Date startDate = remainingTask.getReleaseDate();
		if (prevJob.getEnd().compareTo(startDate) > 0) {
			startDate = prevJob.getEnd();
		}
		return new Date(startDate.toSeconds() + getProductionTime(prevJob, null, remainingTask));
	}

	@Override
	public void insert(T task) {
		if (schedule.isEmpty()) {
			Job<T> job = new Job<>(task.getReleaseDate(),
					new Date(task.getReleaseDate().toSeconds() + getProductionTime(null, null, task)), task);
			schedule.add(job);
			return;
		}
		Job<T> existingJob = getJob(task.getProductId());
		if (existingJob != null) {
			return;
		}
		List<Job<T>> newJobs = new LinkedList<>();

		T remainingTask = (T) task.copy();
		Job<T> prevJob = null;
		for (int i = 0; i < schedule.size(); i++) {
			Job<T> currentJob = schedule.get(i);
			// Check if and how much we can add between the previous and current
			// job
			T addableTask = addBetweenJobs(prevJob, currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				Date startDate = task.getReleaseDate();
				if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
					startDate = prevJob.getEnd();
				}
				Date endDate = new Date(startDate.toSeconds() + getProductionTime(prevJob, currentJob, addableTask));
				Job<T> job = new Job<>(startDate, endDate, addableTask);
				newJobs.add(job);
				if (remainingTask.getNumOfItems() <= 0) {
					break;
				}
			}
			// Check if and how much we can add to an existing job
			addableTask = addToJob(currentJob, remainingTask);
			if (addableTask != null) {
				int items = addableTask.getNumOfItems();
				remainingTask.setNumOfItems(remainingTask.getNumOfItems() - items);
				currentJob.addTask(addableTask);
				if (remainingTask.getNumOfItems() <= 0) {
					break;
				}
			}

			prevJob = currentJob;
		}
		// Schedule remaining task at the end
		if (remainingTask.getNumOfItems() > 0) {
			Date startDate = remainingTask.getReleaseDate();
			if (prevJob.getEnd().compareTo(startDate) > 0) {
				startDate = prevJob.getEnd();
			}
			Date endDate = new Date(startDate.toSeconds() + getProductionTime(prevJob, null, remainingTask));
			Job<T> job = new Job<>(startDate, endDate, remainingTask);
			newJobs.add(job);
		}
		schedule.addAll(newJobs);
		Collections.sort(schedule);
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

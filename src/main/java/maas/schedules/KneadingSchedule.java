package maas.schedules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import maas.interfaces.Schedule;
import maas.objects.Date;
import maas.tasks.KneadingTask;
import maas.tasks.ScheduledTask;

public class KneadingSchedule implements Schedule<KneadingTask> {

	private List<ScheduledTask<KneadingTask>> schedule;

	public KneadingSchedule() {
		schedule = new LinkedList<>();
	}

	@Override
	public Date getEarliestCompletionTime(KneadingTask task) {
		long releaseDateInSeconds = task.getReleaseDate().toSeconds();
		if (schedule.isEmpty()) {
			long completionTimeInSeconds = releaseDateInSeconds + task.getKneadingTime() + task.getRestingTime();
			return new Date(completionTimeInSeconds);
		}
		// Check if the task already exists
		ScheduledTask<KneadingTask> existingTask = getScheduledTask(task.getProductId());
		if (existingTask != null) {
			// The completion time is the end time of the task plus the resting
			// time
			long completionTimeInSeconds = existingTask.getEnd().toSeconds() + task.getRestingTime();
			return new Date(completionTimeInSeconds);
		}
		Date startDate = task.getReleaseDate();
		for (ScheduledTask<KneadingTask> scheduledTask : schedule) {
			// Check whether we can schedule the task before the scheduled task
			long availableTime = scheduledTask.getStart().toSeconds() - startDate.toSeconds();
			if (task.getKneadingTime() <= availableTime) {
				long completionTimeInSeconds = startDate.toSeconds() + task.getKneadingTime() + task.getRestingTime();
				return new Date(completionTimeInSeconds);
			}
			if (startDate.compareTo(scheduledTask.getEnd()) < 0) {
				startDate = scheduledTask.getEnd();
			}
		}
		// The task can only be scheduled after all existing tasks
		long completionTimeInSeconds = startDate.toSeconds() + task.getKneadingTime() + task.getRestingTime();
		return new Date(completionTimeInSeconds);
	}

	@Override
	public void insert(KneadingTask task) {
		// Check if the task already exists
		if (getScheduledTask(task.getProductId()) != null) {
			return;
		}
		// Get the completion time
		Date endDate = getEarliestCompletionTime(task);
		long completionTime = endDate.toSeconds() - task.getRestingTime();
		long startTime = completionTime - task.getKneadingTime();
		ScheduledTask<KneadingTask> newTask = new ScheduledTask<>(new Date(startTime), new Date(completionTime), task);
		// Insert task and sort
		schedule.add(newTask);
		Collections.sort(schedule);
	}

	@Override
	public ScheduledTask<KneadingTask> getNextScheduledTask() {
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

	/**
	 * Search for a scheduled task producing the item with productId
	 * 
	 * @param productId
	 * @return scheduled task or null if not exists
	 */
	public ScheduledTask<KneadingTask> getScheduledTask(String productId) {
		for (ScheduledTask<KneadingTask> scheduledTask : schedule) {
			if (scheduledTask.getTask().getProductId().equals(productId)) {
				return scheduledTask;
			}
		}
		return null;
	}

}

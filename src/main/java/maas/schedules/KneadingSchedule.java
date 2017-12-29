package maas.schedules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import maas.interfaces.Schedule;
import maas.tasks.KneadingTask;
import maas.tasks.ScheduledTask;

public class KneadingSchedule implements Schedule<KneadingTask> {

	private List<ScheduledTask<KneadingTask>> schedule;

	public KneadingSchedule() {
		schedule = new LinkedList<>();
	}

	@Override
	public long getEarliestCompletionTime(KneadingTask task) {
		if (schedule.isEmpty()) {
			return task.getReleaseDate() + task.getKneadingTime() + task.getRestingTime();
		}
		// Check if the task already exists
		ScheduledTask<KneadingTask> existingTask = getScheduledTask(task.getProductId());
		if (existingTask != null) {
			// The completion time is the end time of the task plus the resting
			// time
			return existingTask.getEnd() + task.getRestingTime();
		}
		long start = task.getReleaseDate();
		for (ScheduledTask<KneadingTask> scheduledTask : schedule) {
			long completionTime = start + task.getKneadingTime();
			// Check whether we can schedule the task before the scheduled task
			if (completionTime <= scheduledTask.getStart()) {
				return completionTime + task.getRestingTime();
			}
			start = Math.max(start, scheduledTask.getEnd());
		}
		return start + task.getKneadingTime() + task.getRestingTime();
	}

	@Override
	public void insert(KneadingTask task) {
		// Check if the task already exists
		if (getScheduledTask(task.getProductId()) != null) {
			return;
		}
		// Get the completion time
		long completionTime = getEarliestCompletionTime(task) - task.getRestingTime();
		ScheduledTask<KneadingTask> newTask = new ScheduledTask<>(completionTime - task.getKneadingTime(),
				completionTime, task);
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

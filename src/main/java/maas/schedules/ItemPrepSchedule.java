package maas.schedules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import maas.interfaces.Schedule;
import maas.objects.Date;
import maas.tasks.ItemPrepTask;
import maas.tasks.ScheduledTask;

public class ItemPrepSchedule implements Schedule<ItemPrepTask> {

	private List<ScheduledTask<ItemPrepTask>> schedule;

	public ItemPrepSchedule() {
		this.schedule = new LinkedList<>();
	}

	@Override
	public Date getEarliestCompletionTime(ItemPrepTask task) {
		long completionTimeInSeconds;
		if (schedule.isEmpty()) {
			completionTimeInSeconds = task.getReleaseDate().toSeconds() + task.getNumOfItems() * task.getItemPrepTime();
			return new Date(completionTimeInSeconds);
		}
		Date startDate = task.getReleaseDate();
		int remainingItems = task.getNumOfItems();
		for (ScheduledTask<ItemPrepTask> scheduledTask : schedule) {
			// Check how much time we have between the last task and the next
			// task
			long availableTime = scheduledTask.getStart().toSeconds() - startDate.toSeconds();
			// Check how many items we could fit in that slot
			int items = (int) (availableTime / task.getItemPrepTime());
			// Check if all remainingItems fit
			if (remainingItems - items <= 0) {
				completionTimeInSeconds = startDate.toSeconds() + remainingItems * task.getItemPrepTime();
				return new Date(completionTimeInSeconds);
			} else if (items > 0) {
				remainingItems = remainingItems - items;
			}
			if (startDate.compareTo(scheduledTask.getEnd()) < 0) {
				startDate = scheduledTask.getEnd();
			}
		}
		completionTimeInSeconds = startDate.toSeconds() + remainingItems * task.getItemPrepTime();
		return new Date(completionTimeInSeconds);
	}

	@Override
	public void insert(ItemPrepTask task) {
		if (schedule.isEmpty()) {
			Date completionDate = new Date(
					task.getReleaseDate().toSeconds() + task.getNumOfItems() * task.getItemPrepTime());
			schedule.add(new ScheduledTask<>(task.getReleaseDate(), completionDate, task));
			return;
		}
		List<ScheduledTask<ItemPrepTask>> subtasks = new LinkedList<>();
		Date startDate = task.getReleaseDate();
		int remainingItems = task.getNumOfItems();
		for (ScheduledTask<ItemPrepTask> scheduledTask : schedule) {
			// Check how much time we have between the last task and the next
			// task
			long availableTime = scheduledTask.getStart().toSeconds() - startDate.toSeconds();
			// Check how many items fit in that slot
			int items = (int) (availableTime / task.getItemPrepTime());
			// Check if all remainingItems fit
			if (remainingItems - items <= 0) {
				ItemPrepTask subtask = createSubTask(task, remainingItems);

				long completionTimeInSeconds = startDate.toSeconds() + remainingItems * task.getItemPrepTime();
				Date endDate = new Date(completionTimeInSeconds);
				subtasks.add(new ScheduledTask<>(startDate, endDate, subtask));
				remainingItems = 0;
				break;
			} else if (items > 0) {
				// Add a new subtask
				ItemPrepTask subtask = createSubTask(task, items);

				long completionTimeInSeconds = startDate.toSeconds() + items * task.getItemPrepTime();
				Date endDate = new Date(completionTimeInSeconds);
				subtasks.add(new ScheduledTask<>(startDate, endDate, subtask));
				remainingItems = remainingItems - items;
			}
			if (startDate.compareTo(scheduledTask.getEnd()) < 0) {
				startDate = scheduledTask.getEnd();
			}
		}
		// Check if items still remain
		if (remainingItems > 0) {
			ItemPrepTask subtask = createSubTask(task, remainingItems);

			long completionTimeInSeconds = startDate.toSeconds() + remainingItems * task.getItemPrepTime();
			Date endDate = new Date(completionTimeInSeconds);
			subtasks.add(new ScheduledTask<>(startDate, endDate, subtask));
		}
		// Add all new tasks and sort
		schedule.addAll(subtasks);
		Collections.sort(schedule);
	}

	@Override
	public ScheduledTask<ItemPrepTask> getNextScheduledTask() {
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

	private ItemPrepTask createSubTask(ItemPrepTask task, int numOfItems) {
		ItemPrepTask subtask = new ItemPrepTask();
		subtask.setReleaseDate(task.getReleaseDate());
		subtask.setDueDate(task.getDueDate());
		subtask.setOrderId(task.getOrderId());
		subtask.setProductId(task.getProductId());

		subtask.setItemPrepTime(task.getItemPrepTime());
		subtask.setNumOfItems(numOfItems);
		return subtask;
	}

}

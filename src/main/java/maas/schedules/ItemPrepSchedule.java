package maas.schedules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import maas.interfaces.Schedule;
import maas.tasks.ItemPrepTask;
import maas.tasks.ScheduledTask;

public class ItemPrepSchedule implements Schedule<ItemPrepTask> {

	private List<ScheduledTask<ItemPrepTask>> schedule;

	public ItemPrepSchedule() {
		this.schedule = new LinkedList<>();
	}

	@Override
	public long getEarliestCompletionTime(ItemPrepTask task) {
		if (schedule.isEmpty()) {
			return task.getReleaseDate() + task.getNumOfItems() * task.getItemPrepTime();
		}
		long start = task.getReleaseDate();
		int remainingItems = task.getNumOfItems();
		for (ScheduledTask<ItemPrepTask> scheduledTask : schedule) {
			// Check how much time we have between the last task and the next
			// task
			long availableTime = scheduledTask.getStart() - start;
			// Check how many items we could fit in that slot
			int items = (int) (availableTime / task.getItemPrepTime());
			// Check if all remainingItems fit
			if (remainingItems - items <= 0) {
				return start + remainingItems * task.getItemPrepTime();
			} else if (items > 0) {
				remainingItems = remainingItems - items;
			}
			start = Math.max(start, scheduledTask.getEnd());
		}
		return start + remainingItems * task.getItemPrepTime();
	}

	@Override
	public void insert(ItemPrepTask task) {
		if (schedule.isEmpty()) {
			schedule.add(new ScheduledTask<>(task.getReleaseDate(),
					task.getReleaseDate() + task.getNumOfItems() * task.getItemPrepTime(), task));
			return;
		}
		List<ScheduledTask<ItemPrepTask>> subtasks = new LinkedList<>();
		long start = task.getReleaseDate();
		int remainingItems = task.getNumOfItems();
		for (ScheduledTask<ItemPrepTask> scheduledTask : schedule) {
			// Check how much time we have between the last task and the next
			// task
			long availableTime = scheduledTask.getStart() - start;
			// Check how many items fit in that slot
			int items = (int) (availableTime / task.getItemPrepTime());
			// Check if all remainingItems fit
			if (remainingItems - items <= 0) {
				ItemPrepTask subtask = new ItemPrepTask(remainingItems, task.getItemPrepTime(), task.getDueDate(),
						task.getReleaseDate(), task.getOrderId(), task.getProductId());
				subtasks.add(new ScheduledTask<>(start, start + remainingItems * task.getItemPrepTime(), subtask));
				break;
			} else if (items > 0) {
				// Add a new subtask
				ItemPrepTask subtask = new ItemPrepTask(items, task.getItemPrepTime(), task.getDueDate(),
						task.getReleaseDate(), task.getOrderId(), task.getProductId());
				subtasks.add(new ScheduledTask<>(start, start + items * task.getItemPrepTime(), subtask));
				remainingItems = remainingItems - items;
			}
			start = Math.max(start, scheduledTask.getEnd());
		}
		// Check if items still remain
		if (remainingItems > 0) {
			ItemPrepTask subtask = new ItemPrepTask(remainingItems, task.getItemPrepTime(), task.getDueDate(),
					task.getReleaseDate(), task.getOrderId(), task.getProductId());
			subtasks.add(new ScheduledTask<>(start, start + remainingItems * task.getItemPrepTime(), subtask));
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

}

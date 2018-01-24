package org.pieceofcake.schedules;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.ItemPrepTask;

public class ItemPrepSchedule extends ProductionSchedule<ItemPrepTask> {

	private static final long serialVersionUID = 3906667994836915492L;

	@Override
	public long getProductionTime(Job<ItemPrepTask> prevJob, Job<ItemPrepTask> nextJob, ItemPrepTask task) {
		return task.getNumOfItems() * task.getItemPrepTime();
	}

	@Override
	public ItemPrepTask addToJob(Job<ItemPrepTask> job, ItemPrepTask task) {
		return null;
	}

	@Override
	public ItemPrepTask addBetweenJobs(Job<ItemPrepTask> prevJob, Job<ItemPrepTask> nextJob, ItemPrepTask task) {
		Date startDate = task.getReleaseDate();
		if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
			startDate = prevJob.getEnd();
		}
		long availableTime = 12*60*60l;
		if (nextJob != null) {
			availableTime = nextJob.getStart().toSeconds() - startDate.toSeconds();
		}
		int items = Math.min((int) (availableTime / task.getItemPrepTime()), task.getNumOfItems());
		if (items > 0) {
			ItemPrepTask subtask = task.copy();
			subtask.setNumOfItems(items);
			return subtask;
		}
		return null;
	}

	@Override
	public Job<ItemPrepTask> getJob(String productId) {
		return null;
	}

}

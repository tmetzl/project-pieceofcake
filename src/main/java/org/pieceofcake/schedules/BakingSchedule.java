package org.pieceofcake.schedules;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.BakingTask;

public class BakingSchedule extends ProductionSchedule<BakingTask> {

	private static final long serialVersionUID = 7811491564775531237L;

	private long coolingRate;
	private long heatingRate;
	private long initialTemperature;

	public BakingSchedule(long coolingRate, long heatingRate, long initialTemperature) {
		super();
		this.coolingRate = coolingRate;
		this.heatingRate = heatingRate;
		this.initialTemperature = initialTemperature;
	}

	@Override
	public long getProductionTime(Job<BakingTask> prevJob, Job<BakingTask> nextJob, BakingTask task) {
		long prevTemp = initialTemperature;
		if (prevJob != null) {
			prevTemp = prevJob.getAssociatedTasks().get(0).getBakingTemperature();
		}
		long tempDifferenceStart = task.getBakingTemperature() - prevTemp;
		long heatingTime = 0;
		if (tempDifferenceStart < 0) {
			heatingTime = -tempDifferenceStart * coolingRate;
		} else {
			heatingTime = tempDifferenceStart * heatingRate;
		}
		long coolingTime = 0;
		if (nextJob != null) {
			long nextTemp = nextJob.getAssociatedTasks().get(0).getBakingTemperature();
			long tempDifferenceEnd = nextTemp - task.getBakingTemperature();
			if (tempDifferenceEnd < 0) {
				coolingTime = -tempDifferenceEnd * coolingRate;
			} else {
				coolingTime = tempDifferenceEnd * heatingRate;
			}
		}

		return heatingTime + task.getBakingTime() + coolingTime;
	}

	@Override
	public BakingTask addToJob(Job<BakingTask> job, BakingTask task) {
		int amount = 0;
		for (BakingTask jobTask : job.getAssociatedTasks()) {
			amount += jobTask.getNumOfItems();
		}
		int remainingItems = task.getItemPerTray() - amount;
		if (remainingItems > 0) {
			BakingTask subTask = task.copy();
			int subAmount = Math.min(remainingItems, task.getNumOfItems());
			subTask.setNumOfItems(subAmount);
			return subTask;

		} else {
			return null;
		}
	}

	@Override
	public BakingTask addBetweenJobs(Job<BakingTask> prevJob, Job<BakingTask> nextJob, BakingTask task) {
		Date startDate = task.getReleaseDate();
		if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
			startDate = prevJob.getEnd();
		}
		long availableTime = 12*60*60l;
		if (nextJob != null) {
			availableTime = nextJob.getStart().toSeconds() - startDate.toSeconds();
		}
		long productionTime = getProductionTime(prevJob, nextJob, task);
		if (availableTime >= productionTime) {
			int subAmount = Math.min(task.getItemPerTray(), task.getNumOfItems());
			BakingTask subTask = task.copy();
			subTask.setNumOfItems(subAmount);
			return subTask;
		}
		return null;
	}

	@Override
	public Job<BakingTask> getJob(String productId) {
		return null;
	}

}

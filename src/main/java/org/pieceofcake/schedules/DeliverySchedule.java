package org.pieceofcake.schedules;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Location;
import org.pieceofcake.streetnetwork.StreetNetwork;
import org.pieceofcake.tasks.DeliveryTask;

public class DeliverySchedule extends ProductionSchedule<DeliveryTask> {

	private static final long serialVersionUID = -2548986266669169133L;

	private StreetNetwork network;
	private Location location;
	private double speed;
	private int boxesPerTruck;

	public DeliverySchedule(StreetNetwork network, Location location, double speed, int boxesPerTruck) {
		this.network = network;
		this.location = location;
		this.speed = speed;
		this.boxesPerTruck = boxesPerTruck;

	}

	public long getJobDuration(List<DeliveryTask> tasks) {
		Location prevLocation = location;
		double distance = 0d;
		for (DeliveryTask task : tasks) {
			distance += network.getDistance(prevLocation, task.getLocation());
			prevLocation = task.getLocation();
		}
		distance += network.getDistance(prevLocation, location);
		return (long) (distance / speed);
	}

	public Date getTaskEnd(Job<DeliveryTask> job, DeliveryTask task) {
		Location prevLocation = location;
		double distance = 0d;
		for (DeliveryTask jobTask : job.getAssociatedTasks()) {
			distance += network.getDistance(prevLocation, jobTask.getLocation());
			prevLocation = jobTask.getLocation();
			if (jobTask.equals(task)) {
				break;
			}
		}
		return new Date(job.getStart().toSeconds() + (long) (distance / speed));
	}

	public long getProductionTime(Job<DeliveryTask> prevJob, Job<DeliveryTask> nextJob, DeliveryTask task) {
		// Drive from the bakery to our new location and back
		double distance = network.getDistance(location, task.getLocation())
				+ network.getDistance(task.getLocation(), location);

		return (long) (distance / speed);
	}

	@Override
	public DeliveryTask addToJob(Job<DeliveryTask> job, DeliveryTask task) {
		if (job.getStart().compareTo(task.getReleaseDate()) < 0) {
			return null;
		}
		int remainingBoxes = boxesPerTruck;

		for (DeliveryTask jobTask : job.getAssociatedTasks()) {
			remainingBoxes -= (jobTask.getNumOfItems() + jobTask.getItemPerBox() - 1) / jobTask.getItemPerBox();
		}
		int boxesNeeded = (task.getNumOfItems() + task.getItemPerBox() - 1) / task.getItemPerBox();
		int boxesFitting = Math.min(remainingBoxes, boxesNeeded);
		if (boxesFitting > 0) {
			DeliveryTask subtask = task.copy();
			subtask.setNumOfItems(Math.min(task.getNumOfItems(), boxesFitting * task.getItemPerBox()));
			ListIterator<DeliveryTask> iter = job.getAssociatedTasks().listIterator();
			while (iter.hasNext()) {
				DeliveryTask jobTask = iter.next();
				if (jobTask.getLocation().equals(task.getLocation())) {
					return subtask;
				}
			}

		}
		return null;
	}

	@Override
	public List<DeliveryTask> addBetweenJobs(Job<DeliveryTask> prevJob, Job<DeliveryTask> nextJob, DeliveryTask task) {
		Date startDate = task.getReleaseDate();
		if (prevJob != null && startDate.compareTo(prevJob.getEnd()) < 0) {
			startDate = prevJob.getEnd();
		}
		long availableTime = 24 * 60 * 60l;
		if (nextJob != null) {
			availableTime = nextJob.getStart().toSeconds() - startDate.toSeconds();
		}

		// Integer division with round up
		int boxesNeeded = (task.getNumOfItems() + task.getItemPerBox() - 1) / task.getItemPerBox();
		int toursNeeded = (boxesNeeded + boxesPerTruck - 1) / boxesPerTruck;
		long timePerTour = (long) ((network.getDistance(location, task.getLocation())
				+ network.getDistance(task.getLocation(), location)) / speed);
		if (timePerTour < 1) {
			timePerTour = 1;
		}
		long toursPossible = availableTime / timePerTour;
		int tours = Math.min(toursNeeded, (int) toursPossible);
		List<DeliveryTask> subtasks = new LinkedList<>();
		int remainingItems = task.getNumOfItems();

		for (int i = 0; i < tours; i++) {
			int subamount = Math.min(task.getItemPerBox() * boxesPerTruck, remainingItems);
			DeliveryTask subtask = task.copy();
			subtask.setNumOfItems(subamount);
			subtasks.add(subtask);
			remainingItems -= subamount;
		}
		return subtasks;
	}

	@Override
	public Job<DeliveryTask> getJob(String productId) {
		return null;
	}

	@Override
	public Date getEarliestCompletionTime(DeliveryTask task) {
		List<Job<DeliveryTask>> jobs = createJobs(task, false);
		Job<DeliveryTask> job;
		if (jobs.isEmpty()) {
			job = getJob(task.getProductId());
		} else {
			job = jobs.get(jobs.size() - 1);
		}
		return getTaskEnd(job, task);
	}

}

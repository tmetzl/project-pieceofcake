package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.behaviours.WaitForResources;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.schedules.DeliverySchedule;
import org.pieceofcake.streetnetwork.StreetNetwork;
import org.pieceofcake.tasks.DeliveryTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class DeliveryMachine extends SingleMachine<DeliveryTask> {

	private static final long serialVersionUID = 3382167581464168853L;
	
	private Map<Integer, Schedule<DeliveryTask>> schedules;
	private double speed;
	private int boxesPerTruck;
	private Location location;
	private StreetNetwork network;
	
	public DeliveryMachine(Location location, String bakeryName, double speed, int boxesPerTruck, StreetNetwork network) {
		super(bakeryName, Services.DELIVERY, Protocols.DELIVERY);
		this.schedules = new HashMap<>();
		this.speed = speed;
		this.boxesPerTruck = boxesPerTruck;
		this.location = location;
		this.network = network;
	}

	@Override
	public Schedule<DeliveryTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new DeliverySchedule(network, location, speed, boxesPerTruck));
	}

	@Override
	public DeliveryTask getTask(String msg) {
		DeliveryTask task = new DeliveryTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}
	
	public List<Resource> getResources(Job<DeliveryTask> job) {
		Map<String, Integer> productAmounts = new HashMap<>();
		for (DeliveryTask task : job.getAssociatedTasks()) {
			int amount = productAmounts.computeIfAbsent(task.getProductId(), k -> 0);
			amount += task.getNumOfItems();
			productAmounts.put(task.getProductId(), amount);
		}
		List<Resource> resources = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : productAmounts.entrySet()) {
			Resource resource = new Resource();
			resource.setResourceType(Resources.COOLED_ITEM);
			resource.setProductId(entry.getKey());
			resource.setAmount(entry.getValue());
			resources.add(resource);
		}
		return resources;
	}

	@Override
	public Behaviour getJobProcessor(Job<DeliveryTask> job) {
		SequentialBehaviour seq = new SequentialBehaviour();
		List<Resource> resources = getResources(job);
		for (Resource resource: resources) {
			seq.addSubBehaviour(new WaitForResources(resource, getBakeryName()));
		}
		Location prevLocation = location;
		for (DeliveryTask task : job.getAssociatedTasks()) {
			double distance = network.getDistance(prevLocation, task.getLocation());
			prevLocation = task.getLocation();
			long duration = (long) (distance/speed);
			seq.addSubBehaviour(new WaitForDuration(duration));
			// Add update stuff here
		}
		long duration = (long) (network.getDistance(prevLocation, location)/speed);
		seq.addSubBehaviour(new WaitForDuration(duration));
		return seq;
	}
	
	

}

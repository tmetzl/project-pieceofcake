package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.behaviours.UpdateResources;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.behaviours.WaitForResources;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.schedules.BakingSchedule;
import org.pieceofcake.tasks.BakingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class BakingMachine extends SingleMachine<BakingTask> {

	private static final long serialVersionUID = 745593275455406705L;
	
	private Map<Integer, Schedule<BakingTask>> schedules;
	private String bakeryName;
	private long initialTemp;
	private long heatingRate;
	private long coolingRate;

	public BakingMachine(String bakeryName, long initialTemp, long heatingRate, long coolingRate) {
		this.bakeryName = bakeryName;
		this.initialTemp = initialTemp;
		this.heatingRate = heatingRate;
		this.coolingRate = coolingRate;
		this.schedules = new HashMap<>();
	}

	@Override
	public Schedule<BakingTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new BakingSchedule(coolingRate, heatingRate, initialTemp));
	}

	@Override
	public BakingTask getTask(String msg) {
		BakingTask task = new BakingTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}

	@Override
	public Behaviour getJobProcessor(Job<BakingTask> job) {
		Resource resourceNeeded = new Resource();
		resourceNeeded.setResourceType(Resources.PREPPED_ITEM);
		resourceNeeded.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (BakingTask task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resourceNeeded.setAmount(amount);
		
		Resource resourceProduced = new Resource();
		resourceProduced.setResourceType(Resources.BAKED_ITEM);
		resourceProduced.setAmount(amount);
		resourceProduced.setProductId(resourceNeeded.getProductId());
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();
		
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForResources(resourceNeeded, bakeryName));		
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(resourceProduced, bakeryName));
		return seq;
	}

	@Override
	public String getServiceType() {
		return Services.BAKE;
	}

	@Override
	public String getBakeryName() {
		return bakeryName;
	}

	@Override
	public String getProtocol() {
		return Protocols.BAKE;
	}
}

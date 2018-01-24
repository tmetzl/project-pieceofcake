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
import org.pieceofcake.schedules.RestingSchedule;
import org.pieceofcake.tasks.RestingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class RestingMachine extends InfiniteParallelMachine<RestingTask> {
	
	private static final long serialVersionUID = -8717639294004713229L;

	private Map<Integer, Schedule<RestingTask>> schedules;	
	private String bakeryName;
	
	public RestingMachine(String bakeryName) {
		this.bakeryName = bakeryName;
		this.schedules = new HashMap<>();
	}

	@Override
	public Schedule<RestingTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new RestingSchedule());
	}

	@Override
	public RestingTask getTask(String msg) {
		RestingTask task = new RestingTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}

	@Override
	public Behaviour getJobProcessor(Job<RestingTask> job) {
		Resource resourceNeeded = new Resource();
		resourceNeeded.setResourceType(Resources.FRESH_DOUGH);
		resourceNeeded.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (RestingTask task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resourceNeeded.setAmount(amount);
		
		Resource resourceProduced = new Resource();
		resourceProduced.setResourceType(Resources.RESTED_DOUGH);
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
		return Services.REST;
	}

	@Override
	public String getBakeryName() {
		return bakeryName;
	}

	@Override
	public String getProtocol() {
		return Protocols.REST;
	}

}

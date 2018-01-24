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
import org.pieceofcake.schedules.CoolingSchedule;
import org.pieceofcake.tasks.CoolingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class CoolingMachine extends InfiniteParallelMachine<CoolingTask> {

	private static final long serialVersionUID = 8600727578291474365L;
	
	private Map<Integer, Schedule<CoolingTask>> schedules;	
	private String bakeryName;
	
	public CoolingMachine(String bakeryName) {
		this.bakeryName = bakeryName;
		this.schedules = new HashMap<>();
	}

	@Override
	public Schedule<CoolingTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new CoolingSchedule());
	}

	@Override
	public CoolingTask getTask(String msg) {
		CoolingTask task = new CoolingTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}

	@Override
	public Behaviour getJobProcessor(Job<CoolingTask> job) {
		Resource resourceNeeded = new Resource();
		resourceNeeded.setResourceType(Resources.BAKED_ITEM);
		resourceNeeded.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (CoolingTask task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resourceNeeded.setAmount(amount);
		
		Resource resourceProduced = new Resource();
		resourceProduced.setResourceType(Resources.COOLED_ITEM);
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
		return Services.COOL;
	}

	@Override
	public String getBakeryName() {
		return bakeryName;
	}

	@Override
	public String getProtocol() {
		return Protocols.COOL;
	}

}

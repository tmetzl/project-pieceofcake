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
import org.pieceofcake.schedules.ItemPrepSchedule;
import org.pieceofcake.tasks.ItemPrepTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class ItemPrepMachine extends SingleMachine<ItemPrepTask> {

	private static final long serialVersionUID = -4022008370196820691L;

	private Map<Integer, Schedule<ItemPrepTask>> schedules;
	private String bakeryName;

	public ItemPrepMachine(String bakeryName) {
		this.bakeryName = bakeryName;
		this.schedules = new HashMap<>();
	}

	@Override
	public Schedule<ItemPrepTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new ItemPrepSchedule());
	}

	@Override
	public ItemPrepTask getTask(String msg) {
		ItemPrepTask task = new ItemPrepTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}

	@Override
	public Behaviour getJobProcessor(Job<ItemPrepTask> job) {
		Resource resourceNeeded = new Resource();
		resourceNeeded.setResourceType(Resources.RESTED_DOUGH);
		resourceNeeded.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (ItemPrepTask task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resourceNeeded.setAmount(amount);
		
		Resource resourceProduced = new Resource();
		resourceProduced.setResourceType(Resources.PREPPED_ITEM);
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
		return Services.PREP;
	}

	@Override
	public String getBakeryName() {
		return bakeryName;
	}

	@Override
	public String getProtocol() {
		return Protocols.PREP;
	}
}

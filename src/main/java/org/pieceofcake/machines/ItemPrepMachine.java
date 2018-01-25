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

	public ItemPrepMachine(String bakeryName) {
		super(bakeryName, Services.PREP, Protocols.PREP);
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
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();		
		SequentialBehaviour seq = new SequentialBehaviour();
		Resource neededResource = getResource(Resources.RESTED_DOUGH, job);
		neededResource.setAmount(1);
		seq.addSubBehaviour(new WaitForResources(neededResource, getBakeryName()));	
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(getResource(Resources.PREPPED_ITEM, job), getBakeryName()));
		return seq;
	}
	
}

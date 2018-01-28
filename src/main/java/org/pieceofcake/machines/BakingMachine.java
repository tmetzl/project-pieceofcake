package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.behaviours.NotifyTaskCompleted;
import org.pieceofcake.behaviours.UpdateResources;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.behaviours.WaitForResources;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.schedules.BakingSchedule;
import org.pieceofcake.tasks.BakingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class BakingMachine extends SingleMachine<BakingTask> {

	private static final long serialVersionUID = 745593275455406705L;
	
	private Map<Integer, Schedule<BakingTask>> schedules;
	private long initialTemp;
	private long heatingRate;
	private long coolingRate;

	public BakingMachine(String bakeryName, long initialTemp, long heatingRate, long coolingRate) {
		super(bakeryName, Services.BAKE, Protocols.BAKE);
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
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();
		
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForResources(getResource(Resources.PREPPED_ITEM, job), getBakeryName()));		
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(getResource(Resources.BAKED_ITEM, job), getBakeryName()));
		for (BakingTask task : job.getAssociatedTasks()) {
			seq.addSubBehaviour(new NotifyTaskCompleted<BakingTask>(getProtocol(), getBakeryName(), task));
		}
		return seq;
	}
	
}

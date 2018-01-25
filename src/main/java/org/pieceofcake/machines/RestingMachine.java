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
import org.pieceofcake.schedules.RestingSchedule;
import org.pieceofcake.tasks.RestingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class RestingMachine extends InfiniteParallelMachine<RestingTask> {
	
	private static final long serialVersionUID = -8717639294004713229L;

	private Map<Integer, Schedule<RestingTask>> schedules;	
	
	public RestingMachine(String bakeryName) {
		super(bakeryName, Services.REST, Protocols.REST);
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
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForResources(getResource(Resources.FRESH_DOUGH, job), getBakeryName()));		
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(getResource(Resources.RESTED_DOUGH, job), getBakeryName()));
		return seq;
	}

}

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
import org.pieceofcake.schedules.CoolingSchedule;
import org.pieceofcake.tasks.CoolingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class CoolingMachine extends InfiniteParallelMachine<CoolingTask> {

	private static final long serialVersionUID = 8600727578291474365L;
	
	private Map<Integer, Schedule<CoolingTask>> schedules;	
	
	public CoolingMachine(String bakeryName) {
		super(bakeryName, Services.COOL, Protocols.COOL);
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
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForResources(getResource(Resources.BAKED_ITEM, job), getBakeryName()));		
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(getResource(Resources.COOLED_ITEM, job), getBakeryName()));
		for (CoolingTask task : job.getAssociatedTasks()) {
			seq.addSubBehaviour(new NotifyTaskCompleted<CoolingTask>(getProtocol(), getBakeryName(), task));
		}
		return seq;
	}

}

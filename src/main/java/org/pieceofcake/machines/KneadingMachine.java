package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.pieceofcake.behaviours.NotifyTaskCompleted;
import org.pieceofcake.behaviours.UpdateResources;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.schedules.KneadingSchedule;
import org.pieceofcake.tasks.KneadingTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;

public class KneadingMachine extends SingleMachine<KneadingTask> {

	private static final long serialVersionUID = 8345855668197258730L;

	private Map<Integer, Schedule<KneadingTask>> schedules;

	public KneadingMachine(String bakeryName) {
		super(bakeryName, Services.KNEAD, Protocols.KNEAD);
		this.schedules = new HashMap<>();
	}

	@Override
	public Schedule<KneadingTask> getScheduleOfDay(int day) {
		return schedules.computeIfAbsent(day, k -> new KneadingSchedule());
	}

	@Override
	public KneadingTask getTask(String msg) {
		KneadingTask task = new KneadingTask();
		task.fromJSONObject(new JSONObject(msg));
		return task;
	}

	@Override
	public Behaviour getJobProcessor(Job<KneadingTask> job) {
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();

		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForDuration(seconds));
		seq.addSubBehaviour(new UpdateResources(getResource(Resources.FRESH_DOUGH, job), getBakeryName()));
		for (KneadingTask task : job.getAssociatedTasks()) {
			seq.addSubBehaviour(new NotifyTaskCompleted<KneadingTask>(getProtocol(), getBakeryName(), task));
		}
		return seq;
	}

}

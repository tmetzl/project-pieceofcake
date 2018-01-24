package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.schedules.KneadingSchedule;
import org.pieceofcake.tasks.KneadingTask;

import jade.core.behaviours.Behaviour;
import jade.util.Logger;

public class KneadingMachine implements Machine<KneadingTask> {

	private static final long serialVersionUID = 8345855668197258730L;

	private Map<Integer, Schedule<KneadingTask>> schedules;
	private Semaphore semaphore;
	private String bakeryName;

	public KneadingMachine(String bakeryName) {
		this.bakeryName = bakeryName;
		this.schedules = new HashMap<>();
		this.semaphore = new Semaphore(1, true);
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
		return new WaitForDuration(seconds);
	}

	@Override
	public void aquireMachine() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Logger.getJADELogger(this.getClass().getName()).log(Logger.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void releaseMachine() {
		semaphore.release();
	}

	@Override
	public String getServiceType() {
		return Services.KNEAD;
	}

	@Override
	public String getBakeryName() {
		return bakeryName;
	}

	@Override
	public String getProtocol() {
		return Protocols.KNEAD;
	}

}

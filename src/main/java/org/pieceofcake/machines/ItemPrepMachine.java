package org.pieceofcake.machines;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;
import org.pieceofcake.behaviours.WaitForDuration;
import org.pieceofcake.behaviours.WaitForResources;
import org.pieceofcake.config.Protocols;
import org.pieceofcake.config.Resources;
import org.pieceofcake.config.Services;
import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.interfaces.Schedule;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.schedules.ItemPrepSchedule;
import org.pieceofcake.tasks.ItemPrepTask;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;

public class ItemPrepMachine implements Machine<ItemPrepTask> {

	private static final long serialVersionUID = -4022008370196820691L;

	private Map<Integer, Schedule<ItemPrepTask>> schedules;
	private Semaphore semaphore;
	private String bakeryName;

	public ItemPrepMachine(String bakeryName) {
		this.bakeryName = bakeryName;
		this.schedules = new HashMap<>();
		this.semaphore = new Semaphore(1, true);
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
		Resource resource = new Resource();
		resource.setResourceType(Resources.RESTED_DOUGH);
		resource.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (ItemPrepTask task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resource.setAmount(amount);
		long seconds = job.getEnd().toSeconds() - job.getStart().toSeconds();
		
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new WaitForResources(resource, bakeryName));		
		seq.addSubBehaviour(new WaitForDuration(seconds));
		return seq;
	}

	@Override
	public void aquireMachine() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Logger.getJADELogger(this.getClass().getName()).log(Logger.WARNING, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void releaseMachine() {
		semaphore.release();
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

package org.pieceofcake.interfaces;

import java.io.Serializable;

import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.Task;

import jade.core.behaviours.Behaviour;

public interface Machine<T extends Task> extends Serializable {
	
	public Schedule<T> getScheduleOfDay(int day);

	public T getTask(String msg);

	public Behaviour getJobProcessor(Job<T> job);
	
	public JobExecutor getJobHandler();
	
	public String getServiceType();
	
	public String getBakeryName();
	
	public String getProtocol();

}

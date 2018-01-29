package org.pieceofcake.interfaces;

import java.io.Serializable;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Job;
import org.pieceofcake.tasks.Task;

public interface Schedule<T extends Task> extends Serializable {
	
	public Date getEarliestCompletionTime(T task);
	
	public void insert(T task);
	
	public Job<T> getNextScheduledJob();
	
	public void removeFirst();
	
	public void removeTasksFromOrder(String orderId);

}

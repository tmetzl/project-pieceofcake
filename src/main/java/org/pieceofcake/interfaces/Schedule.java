package org.pieceofcake.interfaces;

import org.pieceofcake.objects.Date;
import org.pieceofcake.tasks.Job;
import org.pieceofcake.tasks.Task;

public interface Schedule<T extends Task>{
	
	public Date getEarliestCompletionTime(T task);
	
	public void insert(T task);
	
	public Job<T> getNextScheduledJob();
	
	public void removeFirst();
	
	public void removeTasksFromOrder(String orderId);

}

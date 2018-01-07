package org.pieceofcake.interfaces;

import org.pieceofcake.objects.Date;
import org.pieceofcake.tasks.ScheduledTask;
import org.pieceofcake.tasks.Task;

public interface Schedule<T extends Task>{
	
	public Date getEarliestCompletionTime(T task);
	
	public void insert(T task);
	
	public ScheduledTask<T> getNextScheduledTask();
	
	public void removeFirst();

}

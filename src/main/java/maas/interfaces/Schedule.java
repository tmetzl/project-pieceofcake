package maas.interfaces;

import maas.tasks.ScheduledTask;
import maas.tasks.Task;

public interface Schedule<T extends Task>{
	
	public long getEarliestCompletionTime(T task);
	
	public void insert(T task);
	
	public ScheduledTask<T> getNextScheduledTask();
	
	public void removeFirst();

}

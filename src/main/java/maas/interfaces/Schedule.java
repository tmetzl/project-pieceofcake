package maas.interfaces;

import maas.objects.Date;
import maas.tasks.ScheduledTask;
import maas.tasks.Task;

public interface Schedule<T extends Task>{
	
	public Date getEarliestCompletionTime(T task);
	
	public void insert(T task);
	
	public ScheduledTask<T> getNextScheduledTask();
	
	public void removeFirst();

}

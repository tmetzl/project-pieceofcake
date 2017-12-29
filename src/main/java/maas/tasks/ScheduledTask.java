package maas.tasks;

public class ScheduledTask {
	
	private long start;
	private long end;
	private Task task;
	
	public ScheduledTask(long start, long end, Task task) {
		this.start = start;
		this.end = end;
		this.task = task;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public Task getTask() {
		return task;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", start, end, task);
	}

}

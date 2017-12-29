package maas.tasks;

public class ScheduledTask<T extends Task> {
	
	private long start;
	private long end;
	private T task;
	
	public ScheduledTask(long start, long end, T task) {
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

	public T getTask() {
		return task;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", start, end, task);
	}

}

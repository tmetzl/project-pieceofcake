package maas.tasks;

public class ScheduledTask<T extends Task> implements Comparable<ScheduledTask<T>>{
	
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

	@Override
	public int compareTo(ScheduledTask<T> arg0) {
		if (getStart() < arg0.getStart()) {
			return -1;
		} else if (getStart() > arg0.getStart()) {
			return 1;
		}
		return 0;
	}

}

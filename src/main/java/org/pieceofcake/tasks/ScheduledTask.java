package org.pieceofcake.tasks;

import org.pieceofcake.objects.Date;

public class ScheduledTask<T extends Task> implements Comparable<ScheduledTask<T>> {

	private Date start;
	private Date end;
	private T task;

	public ScheduledTask(Date start, Date end, T task) {
		this.start = start;
		this.end = end;
		this.task = task;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public T getTask() {
		return task;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", start.toString(), end.toString(), task);
	}

	@Override
	public int compareTo(ScheduledTask<T> otherTask) {
		return start.compareTo(otherTask.start);
	}

}

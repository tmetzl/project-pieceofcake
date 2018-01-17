package org.pieceofcake.objects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pieceofcake.tasks.Task;

public class Job<T extends Task> {

	private Date start;
	private Date end;
	private List<T> associatedTasks;

	public Job(Date start, Date end, T task) {
		this.start = start;
		this.end = end;
		this.associatedTasks = new LinkedList<>();
		this.associatedTasks.add(task);
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}
	
	public List<T> getAssociatedTasks() {
		return associatedTasks;
	}
	
	public void addTask(T task) {
		associatedTasks.add(task);
	}
	
	public void removeTasksFromOrder(String orderId) {
		Iterator<T> iter = associatedTasks.iterator();
		while (iter.hasNext()) {
			T task = iter.next();
			if (task.getOrderId().equals(orderId)) {
				iter.remove();
			}
		}
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", start.toString(), end.toString(), associatedTasks);
	}

}

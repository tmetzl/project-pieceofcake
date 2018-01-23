package org.pieceofcake.interfaces;

import java.io.Serializable;
import java.util.List;

import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.OrderContract;
import org.pieceofcake.tasks.Task;

import jade.core.AID;

public interface TaskDescriptor<T extends Task> extends Serializable {

	public List<T> prepareTasks();

	public Date getDueDate();

	public void addTaskToOrder(AID agentId, T task, OrderContract contract);

	public String getServiceName();

	public String getBakeryName();

	public String getProtocol();

}

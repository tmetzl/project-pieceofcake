package org.pieceofcake.machines;

import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.tasks.Task;

public abstract class AbstractMachine<T extends Task> implements Machine<T> {

	private static final long serialVersionUID = 7962973446970654581L;
	
	public Resource getResource(String resourceType, Job<T> job) {
		Resource resource = new Resource();
		resource.setResourceType(resourceType);
		resource.setProductId(job.getAssociatedTasks().get(0).getProductId());
		int amount = 0;
		for (T task : job.getAssociatedTasks()) {
			amount += task.getNumOfItems();
		}
		resource.setAmount(amount);
		return resource;
	}

}

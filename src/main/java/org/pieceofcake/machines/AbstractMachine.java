package org.pieceofcake.machines;

import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.objects.Job;
import org.pieceofcake.objects.Resource;
import org.pieceofcake.tasks.Task;

public abstract class AbstractMachine<T extends Task> implements Machine<T> {

	private static final long serialVersionUID = 7962973446970654581L;
	
	private String bakeryName;
	private String serviceType;
	private String protocol;
	
	public AbstractMachine(String bakeryName, String serviceType, String protocol) {
		this.bakeryName = bakeryName;
		this.serviceType = serviceType;
		this.protocol = protocol;
	}

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
	
	@Override
	public String getBakeryName() {
		return bakeryName;
	}
	
	@Override
	public String getServiceType() {
		return serviceType;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

}

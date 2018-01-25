package org.pieceofcake.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Warehouse implements Serializable {

	private static final long serialVersionUID = 5863327946815774727L;

	private Map<String, Integer> resourceStorage;

	public Warehouse() {
		this.resourceStorage = new HashMap<>();
	}

	public void addResource(Resource resource) {
		String resourceKey = resource.getResourceType() + resource.getProductId();

		Integer amount = resourceStorage.get(resourceKey);
		if (amount == null) {
			amount = 0;
		}
		amount += resource.getAmount();
		resourceStorage.put(resourceKey, amount);
	}

	public boolean hasResource(Resource resource) {
		String resourceKey = resource.getResourceType() + resource.getProductId();
		Integer amount = resourceStorage.get(resourceKey);
		if (amount == null) {
			amount = 0;
		}
		return amount >= resource.getAmount();
	}

	public void takeResource(Resource resource) {
		String resourceKey = resource.getResourceType() + resource.getProductId();
		Integer amount = resourceStorage.get(resourceKey);
		if (amount != null && amount >= resource.getAmount()) {
			resourceStorage.put(resourceKey, amount - resource.getAmount());
		} else {
			throw new NoSuchElementException();
		}
	}

	public void clear() {
		resourceStorage.clear();
	}

}

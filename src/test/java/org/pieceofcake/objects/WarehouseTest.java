package org.pieceofcake.objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;
import org.pieceofcake.config.Resources;

public class WarehouseTest {
	
	@Test
	public void testWarehouse() {
		Warehouse warehouse = new Warehouse();
		
		Resource resource = new Resource();
		resource.setResourceType(Resources.RESTED_DOUGH);
		resource.setProductId("Bread");
		resource.setAmount(1);
		
		Resource resource2 = new Resource();
		resource2.setResourceType(Resources.RESTED_DOUGH);
		resource2.setProductId("Pie");
		resource2.setAmount(5);
		
		assertFalse(warehouse.hasResource(resource));
		
		warehouse.addResource(resource);
		
		assertTrue(warehouse.hasResource(resource));
		
		warehouse.takeResource(resource);
		
		assertFalse(warehouse.hasResource(resource));
		
		boolean thrown = false;
		
		try {
			warehouse.takeResource(resource);
		} catch (NoSuchElementException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		thrown = false;
		
		try {
			warehouse.takeResource(resource2);
		} catch (NoSuchElementException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		warehouse.addResource(resource);
		warehouse.clear();
		assertFalse(warehouse.hasResource(resource));
		
	}

}

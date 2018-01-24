package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pieceofcake.config.Resources;

public class ResourceTest {
	
	@Test
	public void testGettersAndSetters() {
		Resource resource = new Resource();
		resource.setResourceType(Resources.RESTED_DOUGH);
		resource.setProductId("Bread");
		resource.setAmount(1);
		
		Resource resourceFromJSON = new Resource();
		resourceFromJSON.fromJSONObject(resource.toJSONObject());
		
		assertEquals(resource.getResourceType(), resourceFromJSON.getResourceType());
		assertEquals(resource.getProductId(), resourceFromJSON.getProductId());
		assertEquals(resource.getAmount(), resourceFromJSON.getAmount());
		
	}

}

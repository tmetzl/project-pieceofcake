package maas.streetnetwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import maas.objects.Location;

public class NodeTest {

	private Node nodeA;
	private Node nodeA1;
	private Node nodeB;
	private Node nodeC;

	@Before
	public void initializeNodes() {
		nodeA = new Node("node-001", "Node A", "Type A", "Company A", new Location(5.4, 7.1));
		nodeA1 = new Node("node-001", "Node A", "Type A", "Company A", new Location(5.4, 7.1));
		nodeB = new Node("node-002", "Node B", "Type B", "Company B", new Location(8.3, 13.5));
		nodeC = new Node("node-003", "Node C", "Type C", "Company C", new Location(5.9, 1.1));
	}

	@Test
	public void nodeGetterTest() {
		assertEquals("node-001", nodeA.getGuid());
		assertEquals("Node A", nodeA.getName());
		assertEquals("Type A", nodeA.getType());
		assertEquals("Company A", nodeA.getCompany());
		assertEquals(5.4, nodeA.getLocationX(), 1e-10);
		assertEquals(7.1, nodeA.getLocationY(), 1e-10);
	}

	@Test
	public void nodeEqualsTest() {
		assertTrue(nodeA.equals(nodeA));
		assertTrue(nodeA.equals(nodeA1));
		assertFalse(nodeA.equals(nodeB));
		assertFalse(nodeA.equals(nodeC));
		assertFalse(nodeA.equals(new Object()));
	}

	@Test
	public void hashCodeTest() {
		assertEquals(nodeA.hashCode(), nodeA1.hashCode());
	}

	@Test
	public void toStringTest() {
		String s = String.format("Node %s: %s from company %s of type %s at (%.2f, %.2f)", "node-003", "Node C", "Company C",
				"Type C", 5.9, 1.1);
		assertEquals(s, nodeC.toString());
	}

}

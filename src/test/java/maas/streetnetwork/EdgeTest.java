package maas.streetnetwork;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EdgeTest {
	
	private Edge edgeA;
	private Edge edgeB;
	
	private Node fromA;
	private Node fromB;
	private Node toA;
	private Node toB;
	
	@Before
	public void initializeEdges() {
		fromA = new Node("node-001", "Node A", "Type A", "Company A", 5.4, 7.1);
		fromB = new Node("node-001", "Node A", "Type A", "Company A", 5.4, 7.1);
		toA = new Node("node-002", "Node B", "Type B", "Company B", 8.3, 13.5);
		toB = new Node("node-003", "Node C", "Type C", "Company C", 5.9, 1.1);
		edgeA = new Edge("edge-001", fromA, toA, 7.2);
		edgeB = new Edge("edge-002", fromB, toB, 3.7);
	}
	
	@Test
	public void edgeGetterTest() {
		assertEquals("edge-001", edgeA.getGuid());
		assertEquals("edge-002", edgeB.getGuid());
		assertEquals(fromA, edgeA.getFrom());
		assertEquals(fromB, edgeB.getFrom());
		assertEquals(toA, edgeA.getTo());
		assertEquals(toB, edgeB.getTo());
		assertEquals(7.2, edgeA.getDist(), 1e-10);
		assertEquals(3.7, edgeB.getDist(), 1e-10);
	}
	
	@Test
	public void toStringTest() {
		String toStringA = String.format("%s: (%s, %s, %.2f)", "edge-001", "node-001", "node-002", 7.2);
		String toStringB = String.format("%s: (%s, %s, %.2f)", "edge-002", "node-001", "node-003", 3.7);
		
		assertEquals(toStringA, edgeA.toString());
		assertEquals(toStringB, edgeB.toString());
	}

}

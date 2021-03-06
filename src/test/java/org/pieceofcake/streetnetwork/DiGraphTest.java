package org.pieceofcake.streetnetwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pieceofcake.objects.Location;

public class DiGraphTest {

	private DiGraph graph;
	private List<Node> nodes;

	@Before
	public void initializeGraph() {
		nodes = new LinkedList<>();

		for (int i = 0; i < 10; i++) {
			nodes.add(new Node("node-" + i, "Node " + i, "Type " + i, "Company " + i, new Location(3.2 + i, 6.2 + i)));
		}

		nodes.add(new Node("node-1", "Node 1", "Type 1", "Company 1", new Location(4.2, 7.2)));

		graph = new DiGraph();

		for (Node node : nodes) {
			graph.addNode(node);
		}

		for (int i = 1; i < 10; i++) {
			graph.addEdge("node-0", "node-" + i, 1.1 + i, "edge-" + i);
		}
	}

	@Test
	public void getNodeTest() {
		for (Node node : nodes) {
			assertEquals(node, graph.getNodeFromId(node.getGuid()));
		}

		boolean thrown = false;
		try {
			graph.getNodeFromId("node_that_doesn't-exist");
		} catch (NoSuchElementException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void getNodesTest() {
		Set<Node> nodesInGraph = graph.getNodes();

		assertTrue(nodesInGraph.containsAll(nodes));

		nodesInGraph.removeAll(nodes);

		assertTrue(nodesInGraph.isEmpty());
	}

	@Test
	public void getEdgeTest() {
		List<Edge> edgesOfNodeOne = graph.getEdgesFromNodeId("node-0");

		assertEquals(9, edgesOfNodeOne.size());

		for (int i = 0; i < edgesOfNodeOne.size(); i++) {
			Edge edge = edgesOfNodeOne.get(i);
			assertEquals(nodes.get(0), edge.getFrom());
			assertEquals(nodes.get(i + 1), edge.getTo());
		}

		for (int i = 1; i < 10; i++) {
			List<Edge> edges = graph.getEdgesFromNodeId("node-" + i);
			assertTrue(edges.isEmpty());
		}

		boolean thrown = false;
		try {
			graph.getEdgesFromNodeId("node_that_doesn't-exist");
		} catch (NoSuchElementException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void hasEdgeTest() {
		for (int i = 1; i < graph.getNodes().size(); i++) {
			assertTrue(graph.hasEdge(nodes.get(0), nodes.get(i)));
		}
		for (int i = 1; i < graph.getNodes().size(); i++) {
			for (int j = 0; j < graph.getNodes().size(); j++) {
				assertFalse(graph.hasEdge(nodes.get(i), nodes.get(j)));
			}
		}
	}

}

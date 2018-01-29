package org.pieceofcake.streetnetwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DiGraph implements Serializable {

	private static final long serialVersionUID = -6116419588894989745L;
	
	private Map<Node, List<Edge>> adjacencyMap;

	public DiGraph() {
		this.adjacencyMap = new HashMap<>();
	}

	public void addNode(Node node) {
		if (!adjacencyMap.containsKey(node)) {
			adjacencyMap.put(node, new LinkedList<Edge>());
		}
	}

	public Node getNodeFromId(String guid) {
		Set<Node> nodes = adjacencyMap.keySet();
		for (Node node : nodes) {
			if (node.getGuid().equals(guid))
				return node;
		}
		throw new NoSuchElementException();
	}

	public void addEdge(String fromGuid, String toGuid, double dist, String edgeGuid) {
		Node nodeA = getNodeFromId(fromGuid);
		Node nodeB = getNodeFromId(toGuid);
		List<Edge> nodesAdjacentToA = adjacencyMap.get(nodeA);
		Edge edge = new Edge(edgeGuid, nodeA, nodeB, dist);
		nodesAdjacentToA.add(edge);
		adjacencyMap.put(nodeA, nodesAdjacentToA);
	}
	
	public boolean hasEdge(Node from, Node to) {
		List<Edge> edges = adjacencyMap.get(from);
		for (Edge edge : edges) {
			if (edge.getTo() == to) {
				return true;
			}
		}
		return false;
	}

	public Set<Node> getNodes() {
		return adjacencyMap.keySet();
	}

	public List<Edge> getEdgesFromNodeId(String guid) {
		return adjacencyMap.get(getNodeFromId(guid));
	}
	
	public List<Edge> getEdges() {
		List<Edge> edges = new LinkedList<>();
		for (List<Edge> value : adjacencyMap.values()) {
			edges.addAll(value);
		}
		return edges;
	}

}

package maas.streetnetwork;

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

	public Node getNode(String guid) {
		Set<Node> nodes = adjacencyMap.keySet();
		for (Node node : nodes) {
			if (node.getGuid().equals(guid))
				return node;
		}
		throw new NoSuchElementException();
	}

	public void addEdge(String fromGuid, String toGuid, double dist, String edgeGuid) {
		Node nodeA = getNode(fromGuid);
		Node nodeB = getNode(toGuid);
		List<Edge> nodesAdjacentToA = adjacencyMap.get(nodeA);
		Edge edge = new Edge(edgeGuid, nodeA, nodeB, dist);
		nodesAdjacentToA.add(edge);
		adjacencyMap.put(nodeA, nodesAdjacentToA);
	}

	public Set<Node> getNodes() {
		return adjacencyMap.keySet();
	}

	public List<Edge> getEdges(String guid) {
		List<Edge> edges = adjacencyMap.get(getNode(guid));
		if (edges == null) {
			edges = new LinkedList<>();
		}
		return edges;
	}

	public static void main(String[] args) {
		new DiGraph();
	}

}

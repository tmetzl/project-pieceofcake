package org.pieceofcake.streetnetwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.pieceofcake.objects.Location;

public class StreetNetwork implements Serializable {
	
	private static final long serialVersionUID = 8549868542763579920L;
	
	private Map<Location, Node> locationMap;
	private Map<Node, Map<Node, Double>> distances;
	private DiGraph graph;
	
	public StreetNetwork(DiGraph graph) {
		this.distances = new HashMap<>();
		this.locationMap = new HashMap<>();
		this.graph = graph;
		init();
		computeDistances();
		
		for (Node node : graph.getNodes()) {
			locationMap.put(node.getLocation(), node);
		}
		
	}
	
	public double getDistance(Node nodeFrom, Node nodeTo) {
		if (!distances.containsKey(nodeFrom) || !distances.get(nodeFrom).containsKey(nodeTo)) {
			return Double.MAX_VALUE;
		}
		return distances.get(nodeFrom).get(nodeTo);
	}
	
	public double getDistance(Location locationA, Location locationB) {
		if (!locationMap.containsKey(locationA) || !locationMap.containsKey(locationB)) {
			return Double.MAX_VALUE;
		}
		return getDistance(locationMap.get(locationA), locationMap.get(locationB));
	}

	private void init() {
		// Set all distances to MAX
		for (Node nodeA : graph.getNodes()) {
			for (Node nodeB : graph.getNodes()) {
				Map<Node, Double> distancesFromA = distances.computeIfAbsent(nodeA, k -> new HashMap<>());
				distancesFromA.put(nodeB, Double.MAX_VALUE);
			}
		}
		// Set all distances between the same node to 0
		for (Node node : graph.getNodes()) {
			distances.get(node).put(node, 0d);
		}
	}

	private void computeDistances() {
		for (Edge edge : graph.getEdges()) {
			distances.get(edge.getFrom()).put(edge.getTo(), edge.getDist());
		}
		for (Node nodeA : graph.getNodes()) {
			for (Node nodeB : graph.getNodes()) {
				for (Node nodeC : graph.getNodes()) {
					double distAC = distances.get(nodeA).get(nodeC);
					double distBC = distances.get(nodeB).get(nodeC);
					double distBA = distances.get(nodeB).get(nodeA);
					if (distBC > distBA + distAC) {
						distances.get(nodeB).put(nodeC, distBA + distAC);
					}
				}
			}
		}
	}
}

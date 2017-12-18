package maas.streetnetwork;

import java.io.Serializable;

public class Edge implements Serializable {

	private static final long serialVersionUID = -3361852624043201226L;
	
	private String guid;
	private Node from;
	private Node to;
	private double dist;

	public Edge(String guid, Node from, Node to, double dist) {
		this.guid = guid;
		this.from = from;
		this.to = to;
		this.dist = dist;
	}

	public String getGuid() {
		return guid;
	}
	
	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	public double getDist() {
		return dist;
	}

	@Override
	public String toString() {
		return String.format("%s: (%s, %s, %.2f)", guid, from.getGuid(), to.getGuid(), dist);
	}

}

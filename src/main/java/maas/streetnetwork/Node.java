package maas.streetnetwork;

import java.io.Serializable;

public class Node implements Serializable {

	private static final long serialVersionUID = 5938195662501825629L;
	
	private String guid;
	private String name;
	private String type;
	private String company;
	private double locationX;
	private double locationY;

	public Node(String guid, String name, String type, String company, double locationX, double locationY) {
		this.company = company;
		this.guid = guid;
		this.name = name;
		this.type = type;
		this.locationX = locationX;
		this.locationY = locationY;
	}

	@Override
	public String toString() {
		return String.format("Node %s: %s from company %s of type %s at (%.2f, %.2f)", guid, name, company, type,
				locationX, locationY);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node node = (Node) o;
			return this.guid.equals(node.guid);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return guid.hashCode();
	}

	public String getCompany() {
		return company;
	}

	public String getGuid() {
		return guid;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getLocationX() {
		return locationX;
	}

	public double getLocationY() {
		return locationY;
	}
}
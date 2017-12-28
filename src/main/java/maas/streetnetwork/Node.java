package maas.streetnetwork;

import java.io.Serializable;

import maas.interfaces.Localizable;
import maas.objects.Location;

public class Node implements Serializable, Localizable {

	private static final long serialVersionUID = 5938195662501825629L;
	
	private String guid;
	private String name;
	private String type;
	private String company;
	private Location location;

	public Node(String guid, String name, String type, String company, Location location) {
		this.company = company;
		this.guid = guid;
		this.name = name;
		this.type = type;
		this.location = location;
	}

	@Override
	public String toString() {
		return String.format("Node %s: %s from company %s of type %s at (%.2f, %.2f)", guid, name, company, type,
				getLocationX(), getLocationY());
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

	@Override
	public double getLocationX() {
		return location.getLocationX();
	}

	@Override
	public double getLocationY() {
		return location.getLocationY();
	}
}
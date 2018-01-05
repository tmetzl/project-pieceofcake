package maas.objects;

import java.io.Serializable;

import org.json.JSONObject;

public class Location implements Serializable {

	private static final long serialVersionUID = -8057452819976425821L;

	private double x;
	private double y;

	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Location() {

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Location) {
			Location location = (Location) o;
			return (x == location.getX() && y == location.getY());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (x + "" + y).hashCode();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("x", x);
		jsonObject.put("y", y);
		return jsonObject;
	}

	public void fromJSONObject(JSONObject jsonObject) {
		setX(jsonObject.getDouble("x"));
		setY(jsonObject.getDouble("y"));
	}
}

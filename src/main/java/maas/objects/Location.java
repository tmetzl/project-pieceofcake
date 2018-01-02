package maas.objects;

import java.io.Serializable;

public class Location implements Serializable {
	
	private static final long serialVersionUID = -8057452819976425821L;
	
	private double x;
	private double y;
	
	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

}

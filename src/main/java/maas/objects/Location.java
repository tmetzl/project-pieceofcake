package maas.objects;

import java.io.Serializable;

import maas.interfaces.Localizable;

public class Location implements Localizable,Serializable {
	
	private static final long serialVersionUID = -8057452819976425821L;
	
	private double x;
	private double y;
	
	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public double getLocationX() {
		return x;
	}
	@Override
	public double getLocationY() {
		return y;
	}

}

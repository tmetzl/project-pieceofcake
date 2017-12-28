package maas.objects;

import maas.interfaces.Localizable;

public class Location implements Localizable {
	
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
		// TODO Auto-generated method stub
		return y;
	}

}

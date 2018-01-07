package org.pieceofcake.interfaces;

public interface BakeryObservable {
	
	public void registerObserver(BakeryObserver observer, String topic);
	
	public void notifyObservers(String topic);

}

package org.pieceofcake.interfaces;

import java.io.Serializable;

import jade.core.Agent;

public interface Wakeable extends Serializable {
	
	public Agent getAgent();
	
	public void wake();

}

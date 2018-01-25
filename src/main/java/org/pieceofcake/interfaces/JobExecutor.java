package org.pieceofcake.interfaces;

import java.io.Serializable;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;

public interface JobExecutor extends Serializable {
	
	public void addSubBehaviour(Behaviour behaviour);
	
	public boolean isRunning();
	
	public CompositeBehaviour getBehaviour();

}

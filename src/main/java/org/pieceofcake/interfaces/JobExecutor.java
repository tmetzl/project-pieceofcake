package org.pieceofcake.interfaces;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;

public interface JobExecutor {
	
	public void addSubBehaviour(Behaviour behaviour);
	
	public boolean isRunning();
	
	public CompositeBehaviour getBehaviour();

}

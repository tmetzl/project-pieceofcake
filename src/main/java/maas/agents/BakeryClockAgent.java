package maas.agents;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;
import maas.objects.Bakery;

public class BakeryClockAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -5502742435059986880L;

	private Bakery myBakery;

	public BakeryClockAgent(Bakery bakery) {
		this.myBakery = bakery;
	}

	@Override
	protected void setup() {
		super.setup();
		// Printout a welcome message
		String welcomeMessage = String.format("Bakery-Clock-Agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock());
		seq.addSubBehaviour(new WaitForStart());
		seq.addSubBehaviour(new MonitorTime());

		addBehaviour(seq);

	}

	private class MonitorTime extends Behaviour {

		private static final long serialVersionUID = -7393395145982480330L;
		
		private int currentDay;

		@Override
		public void onStart() {
			currentDay = getDay();
			myBakery.newDay();
		}

		@Override
		public void action() {
			if (getDay() != currentDay) {
				currentDay = getDay();
				myBakery.newDay();
				String message = String.format("Day is now %d.", currentDay);
				logger.log(Logger.INFO, message);
			} else {
				block(1000);
			}
		}

		@Override
		public boolean done() {
			return false;
		}

	}
}

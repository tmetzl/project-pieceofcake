package maas.agents;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;
import maas.behaviours.SynchronizeClock;
import maas.behaviours.WaitForStart;
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

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new WaitForStart(getScenarioClock()));
		seq.addSubBehaviour(new MonitorTime());

		addBehaviour(seq);

	}

	private class MonitorTime extends Behaviour {

		private static final long serialVersionUID = -7393395145982480330L;
		
		private int currentDay;

		@Override
		public void onStart() {
			currentDay = getScenarioClock().getDate().getDay();
			myBakery.newDay();
		}

		@Override
		public void action() {
			if (getScenarioClock().getDate().getDay() != currentDay) {
				currentDay = getScenarioClock().getDate().getDay();
				myBakery.newDay();
				String message = String.format("Day is now %d.", currentDay);
				logger.log(Logger.INFO, message);
			} else {
				block(100);
			}
		}

		@Override
		public boolean done() {
			return false;
		}

	}
}

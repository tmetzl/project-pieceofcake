package maas.agents;

import jade.core.behaviours.SequentialBehaviour;
import jade.util.Logger;
import maas.behaviours.SynchronizeClock;
import maas.behaviours.DelayUntilDate;
import maas.behaviours.ReceiveStartingTime;
import maas.objects.Bakery;
import maas.objects.Date;

public class BakeryClockAgent extends SynchronizedAgent {

	private static final long serialVersionUID = -5502742435059986880L;

	private Bakery myBakery;

	public BakeryClockAgent(Bakery bakery) {
		this.myBakery = bakery;
		this.location = bakery.getLocation();
	}

	@Override
	protected void setup() {
		// Printout a welcome message
		String welcomeMessage = String.format("Bakery-Clock-Agent %s is ready!", getAID().getLocalName());
		logger.log(Logger.INFO, welcomeMessage);

		SequentialBehaviour seq = new SequentialBehaviour();

		seq.addSubBehaviour(new SynchronizeClock(getScenarioClock()));
		seq.addSubBehaviour(new ReceiveStartingTime(getScenarioClock()));
		seq.addSubBehaviour(new MonitorTime(new Date(1, 0, 0, 0)));

		addBehaviour(seq);
	}

	private class MonitorTime extends SequentialBehaviour {

		private static final long serialVersionUID = -7393395145982480330L;

		private Date date;

		public MonitorTime(Date date) {
			this.date = date;
			this.addSubBehaviour(new DelayUntilDate(getScenarioClock(), date));
		}

		@Override
		public int onEnd() {
			myBakery.newDay();
			String message = String.format("Day is now %d.", date.getDay());
			logger.log(Logger.INFO, message);
			myAgent.addBehaviour(new MonitorTime(new Date(date.getDay() + 1, 0, 0, 0)));
			return 0;
		}

	}
}

package de.gwasch.code.escframework.events.sources;

import java.util.Random;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Inlet;
import de.gwasch.code.escframework.events.utils.TimeUtil;

/**
 * {@code EventGenerator} is an {@code EventSource} that cyclicly creates events
 * based on an {@code EventFactory}.
 * <p>
 * For example, this is used by {@link Inlet}.
 * 
 * @param <E> the event type considered by the {@code EventGenerator}
 */
public class EventGenerator<E extends Event> implements EventSource<E> {

	private EventFactory<E> eventFactory;

	private int avgTickPause;
	private double maxDeviationFactor;

	private Random random;

	/**
	 * Creates an {@code EventGenerator}.
	 * 
	 * @param eventfactory       the event factory which creates single events on
	 *                           demand
	 * @param avgtickpause       average pause between two events
	 * @param maxdeviationfactor maximum deviation factor enables a range of tick
	 *                           pauses
	 */
	public EventGenerator(EventFactory<E> eventfactory, int avgtickpause, double maxdeviationfactor) {

		eventFactory = eventfactory;
		avgTickPause = avgtickpause;
		maxDeviationFactor = maxdeviationfactor;

		random = new Random();
	}

	/**
	 * Returns the event factory.
	 * 
	 * @return the event factory
	 */
	public EventFactory<E> getEventFactory() {
		return eventFactory;
	}

	/**
	 * Sets the events factory.
	 * 
	 * @param eventFactory the event factory which creates single events on demand
	 */
	public void setEventFactory(EventFactory<E> eventFactory) {
		this.eventFactory = eventFactory;
	}

	/**
	 * Returns the average tick pause.
	 * 
	 * @return the average tick pause
	 */
	public int getAvgTickPause() {
		return avgTickPause;
	}

	/**
	 * Sets the avarage tick pause.
	 * 
	 * @param avgTickPause the average tick pause
	 */
	public void setAvgTickPause(int avgTickPause) {
		this.avgTickPause = avgTickPause;
	}

	/**
	 * Returns the maximum deviation factor.
	 * 
	 * @return the maximum deviation factor.
	 */
	public double getMaxDeviationFactor() {
		return maxDeviationFactor;
	}

	/**
	 * Sets the maximum deviation factor.
	 * 
	 * @param maxDeviationFactor the maximum deviation factor.
	 */

	public void setMaxDeviationFactor(double maxDeviationFactor) {
		this.maxDeviationFactor = maxDeviationFactor;
	}

	/**
	 * Asks the event factory to create a new event and sets its push time based on current time,
	 * average tick pause, maximum deviation factor and a random number between 0.0
	 * (inclusive) and lower than 1.0 (exclusive).
	 * 
	 * @return the new event
	 */
	public E pop() {

		double r = random.nextDouble();
		int pause = (int) (avgTickPause - maxDeviationFactor * avgTickPause
				+ 2 * r * maxDeviationFactor * avgTickPause);

		E event = eventFactory.create();
		TimeUtil.setPushTime(event, pause);
		return event;
	}
}

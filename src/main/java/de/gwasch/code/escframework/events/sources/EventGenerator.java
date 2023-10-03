package de.gwasch.code.escframework.events.sources;

import java.util.Random;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Inlet;
import de.gwasch.code.escframework.events.utils.TimeUtil;

/**
 * {@code EventGenerator} is an {@code EventSource} that cyclicly creates events based on an {@code EventFactory}. 
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

	public EventGenerator(EventFactory<E> eventfactory, int avgtickpause, double maxdeviationfactor) {
		
		eventFactory = eventfactory;
		avgTickPause = avgtickpause;
		maxDeviationFactor = maxdeviationfactor;
		
		random = new Random();
	}
	
	public EventFactory<E> getEventFactory() {
		return eventFactory;
	}
	
	public void setEventFactory(EventFactory<E> eventFactory) {
		this.eventFactory = eventFactory;
	}
	
	public int getAvgTickPause() {
		return avgTickPause;
	}
	
	public void setAvgTickPause(int avgTickPause) {
		this.avgTickPause = avgTickPause;
	}

	public double getMaxDeviationFactor() {
		return maxDeviationFactor;
	}

	public void setMaxDeviationFactor(double maxDeviationFactor) {
		this.maxDeviationFactor = maxDeviationFactor;
	}
	
	public E pop() {
		
		double r = random.nextDouble();
		int pause = (int)(avgTickPause - maxDeviationFactor * avgTickPause
				+ 2 * r * maxDeviationFactor * avgTickPause);
		
		E event = eventFactory.create();
		TimeUtil.setPushTime(event, pause);
		return event;
	}
}

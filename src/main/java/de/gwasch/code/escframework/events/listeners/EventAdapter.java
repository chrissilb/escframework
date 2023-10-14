package de.gwasch.code.escframework.events.listeners;

import de.gwasch.code.escframework.events.events.Event;

/** 
 * An abstract adapter class. The methods in this class are empty. 
 * It exists as convenience for creating concrete listeners.
 * 
 * @param <E> the event type considered by the {@code EventListener}
 */
public abstract class EventAdapter<E extends Event> implements EventListener<E> {

	public boolean onProcess(E event) {
		return true;
	}

	public void onFinish(E event, boolean success) {
	}
}

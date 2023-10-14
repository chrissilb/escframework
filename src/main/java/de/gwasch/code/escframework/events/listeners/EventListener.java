package de.gwasch.code.escframework.events.listeners;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Dispatcher;

/**
 * The {@code EventListener} allows to register for process events at the {@link Dispatcher}.
 * 
 * @param <E> the event type considered by the {@code EventListener}
 * 
 * @see Dispatcher
 * @see EventAdapter
 */
public interface EventListener<E extends Event> {
	boolean onProcess(E event);
	void onFinish(E event, boolean success);
}
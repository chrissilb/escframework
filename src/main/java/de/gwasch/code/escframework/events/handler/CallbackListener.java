package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

/**
 * The {@code CallbackListener} enables {@code Processor}s to register for the backward communication of process events.
 * 
 * @param <E> the event type considered by the {@code CallbackListener}
 * 
 * @see Processor
 * @see ProcessListener
 */
public interface CallbackListener<E extends Event> {
	
	void finish(E event, boolean success);
}
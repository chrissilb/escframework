package de.gwasch.code.escframework.events.handler;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

/**
 * {@code ProcessListener} enables {@code Processor}s to register for the forward communication of process events.
 * 
 * @param <E>  the event type considered by the {@code ProcessListener}
 * 
 * @see Processor
 * @see CallbackListener
 */
public interface ProcessListener<E extends Event> {
	
	void process(E event);
}
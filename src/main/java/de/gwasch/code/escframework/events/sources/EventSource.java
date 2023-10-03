package de.gwasch.code.escframework.events.sources;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Inlet;

/**
 * An {@code EventSource} is used to provide new events to the processor network according to an algorithm.
 * <p>
 * For example, this is used by {@link Inlet}.
 * 
 * @param <E> the event type considered by the {@code EventSource} 
 */
public interface EventSource<E extends Event> {

	E pop();
}

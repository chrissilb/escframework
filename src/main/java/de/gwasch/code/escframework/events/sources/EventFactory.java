package de.gwasch.code.escframework.events.sources;

import de.gwasch.code.escframework.events.events.Event;

/**
 * {@code EventFactory} creates new events.
 * <p>
 * It is used by {@link EventGenerator}.
 *  * 
 * @param <E> the event type considered by the {@code EventFactory}
 */
public interface EventFactory<E extends Event> {

	E create();
}

package de.gwasch.code.escframework.states.listeners;

import de.gwasch.code.escframework.states.states.State;

/**
 * An {@code ActivityListener} can block a {@link State} transition.
 * @param <T> the type of the {@code State} value
 */
public interface ActivityListener<T> {
	
	/**
	 * Accepts the transition or not
	 * @param newValue the new {@code State} value
	 * @param oldValue the old {@code State} value
	 * @return {@code true} if transition is accepted; otherwise {@code false}
	 */
	boolean activity(T newValue, T oldValue);
}
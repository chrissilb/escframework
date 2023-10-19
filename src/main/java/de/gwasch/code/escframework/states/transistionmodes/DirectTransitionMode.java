package de.gwasch.code.escframework.states.transistionmodes;

import de.gwasch.code.escframework.states.states.State;

/**
 * {@code DirectTranstionMode} is the default {@code TransitionMode} of a {@link State}.
 * 
 * @param <T> the type of the {@code State} value
 */
public class DirectTransitionMode<T> implements TransitionMode<T> {

	/**
	 * Returns {@code target}.
	 */
	public T singleTransition(T target, T current) {
		return target;
	}
}

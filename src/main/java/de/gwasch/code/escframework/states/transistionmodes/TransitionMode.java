package de.gwasch.code.escframework.states.transistionmodes;

import de.gwasch.code.escframework.states.states.State;

/**
 * A {@code TransitionMode} defines for a {@link State} how to get from a current value to a target value.
 * 
 * @param <T> the type of the {@code State} value
 */
public interface TransitionMode<T> {

	/**
	 * Returns the next state value.
	 * 
	 * @param target the target value set requested for a {@code State}
	 * @param current the current value of a {@code State}
	 * @return the next state value
	 */
	T singleTransition(T target, T current);
}

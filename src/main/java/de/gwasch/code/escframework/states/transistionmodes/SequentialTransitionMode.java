package de.gwasch.code.escframework.states.transistionmodes;

import de.gwasch.code.escframework.states.states.State;
import de.gwasch.code.escframework.states.utils.EnumUtil;

/**
 * {@code SequentialTransitionMode} supports for a {@link State} to get a {@code target} value in a step-wise manner.
 * This is only supported for enum types. Interim steps are defined by its ordinal numbers. 
 * 
 * @param <T> the type of the {@code State} value
 */
public class SequentialTransitionMode<T extends Enum<T>> implements TransitionMode<T> {

	/**
	 * Returns the next higher or lower {@code enum} value to reach the {@code target} value based on the {@code current} value.
	 */
	public T singleTransition(T target, T current) {
		int r = current.compareTo(target);

		if (r < 0) {
			return EnumUtil.increment(current);
		}

		if (r > 0) {
			return EnumUtil.decrement(current);
		}

		return current;
	}
}

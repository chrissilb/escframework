package de.gwasch.code.escframework.states.states;

/**
 * {@code ISState} is an {@link AbstractISState} where {@code strivedState} is a
 * {@link SimpleState}.
 * 
 * @param <T> the type of the {@code State} value
 * 
 * @see SharedISState
 */
public class ISState<T extends Enum<T>> extends AbstractISState<T> {
	public ISState(Class<T> stateType, String name) {
		super(stateType, name, new SimpleState<T>(stateType, name));

	}
}
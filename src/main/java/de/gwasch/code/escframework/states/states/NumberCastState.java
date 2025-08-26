package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.utils.TypeUtil;

/**
 * Converts state values from one numeric type to another numeric type.
 * 
 * @param <T> the target type
 * @param <U> the source type
 */
public class NumberCastState<T extends Number, U extends Number> extends AbstractConversionState<T, U> {

	/**
	 * Constructs a {@code NumberCastState}.
	 * 
	 * @param stateType  class of the target type
	 * @param name       the state name
	 * @param paramState the source state
	 */
	public NumberCastState(Class<T> stateType, String name, State<U> paramState) {
		super(stateType, name, paramState);

		updateValue(paramState.getValue());
	}

	protected void updateValue(U paramValue) {
		setStateValue(TypeUtil.cast(getStateType(), paramValue));
	}
}

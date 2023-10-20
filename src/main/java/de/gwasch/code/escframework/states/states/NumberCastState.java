package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.utils.TypeUtil;

public class NumberCastState<T extends Number, U extends Number> extends AbstractConversionState<T, U> {

	public NumberCastState(Class<T> stateType, String name, State<U> paramState) {
		super(stateType, name, paramState);

		updateValue(paramState.getValue());
	}
	
	protected void updateValue(U paramValue) {
		setStateValue(TypeUtil.cast(getStateType(), paramValue));
	}
}

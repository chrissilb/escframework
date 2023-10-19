package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.exceptions.UnsupportedTypeException;

public class AddState<T extends Number> extends AbstractBinaryState<T> {

	public AddState(Class<T> stateType, String name, State<T> paramState1, State<T> paramState2) {
		super(stateType, name, paramState1, paramState2);

		if (stateType.equals(Short.class) || stateType.equals(Byte.class)) {
			throw new UnsupportedTypeException(stateType);
		}
	}

	@SuppressWarnings("unchecked")
	protected void updateValue(T paramValue1, T paramValue2) {

		if (getStateType().equals(Integer.class)) {
			setStateValue((T) (Integer.valueOf(paramValue1.intValue() + paramValue2.intValue())));
		} else if (getStateType().equals(Double.class)) {
			setStateValue((T) (Double.valueOf(paramValue1.doubleValue() + paramValue2.doubleValue())));
		} else if (getStateType().equals(Long.class)) {
			setStateValue((T) (Long.valueOf(paramValue1.longValue() + paramValue2.longValue())));
		} else /* if (getStateType().equals(Float.class)) */ {
			setStateValue((T) (Float.valueOf(paramValue1.floatValue() + paramValue2.floatValue())));
		}
	}
}

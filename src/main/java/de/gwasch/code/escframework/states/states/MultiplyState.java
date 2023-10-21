package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.exceptions.UnsupportedTypeException;
import de.gwasch.code.escframework.states.utils.TypeUtil;

/**
 * {@code MultiplyState} provides the product of two {@code State} parameters.
 * <p>
 * Supported Number types are Integer, Double, Long and Float. Not supported are Short
 * and Byte because their arithmetic operations return int values.

 * @param <T> the type of the {@code State} value and its parameter {@code State} values
 */
public class MultiplyState<T extends Number> extends AbstractBinaryState<T> {

	public MultiplyState(Class<T> stateType, String name, State<T> paramState1, State<T> paramState2) {
		super(stateType, name, paramState1, paramState2);

		if (stateType.equals(Short.class) || stateType.equals(Byte.class)) {
			throw new UnsupportedTypeException(stateType);
		}
	}

	protected void updateValue(T paramValue1, T paramValue2) {
		setStateValue(TypeUtil.multiply(getStateType(), paramValue1, paramValue2));
	}
}

package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.utils.Mask;

/**
 * A {@code MaskState} converts a state value based on a {@link Mask}.
 * 
 * @param <T> the target {@code State} type
 * @param <U> the source {@code State} type
 */
public class MaskedState<T, U> extends AbstractConversionState<T, U> {

	private Mask<T, U> mask;

	/**
	 * Constructs an {@code MaskedState}.
	 * 
	 * @param stateType  the type of state values
	 * @param name       the state name
	 * @param paramState first state parameter
	 * @param mask       used to map the paramState value to another value which
	 *                   represents the state value of this object
	 */
	public MaskedState(Class<T> stateType, String name, State<U> paramState, Mask<T, U> mask) {
		super(stateType, name, paramState);

		this.mask = mask;
		updateValue(paramState.getValue());
	}

	protected void updateValue(U paramValue) {

		setStateValue(mask.getMaskedValue(paramValue));
	}
}

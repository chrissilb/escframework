package de.gwasch.code.escframework.states.states;

/**
 * {@code SimpleState} is the most basic state type. The set value is
 * immediately the exposed state value.
 * 
 * @param <T> the type of the {@code State} value
 */
public class SimpleState<T> extends State<T> {

	/**
	 * Constructs an {@code SimpleState}.
	 * 
	 * @param stateType the type of state values
	 * @param name the state name
	 */
	public SimpleState(Class<T> stateType, String name) {
		super(stateType, name);
	}

	/**
	 * Constructs an {@code SimpleState} with a give intial value.
	 * 
	 * @param stateType the type of state values
	 * @param name the state name
	 * @param stateValue the initial state value
	 */
	public SimpleState(Class<T> stateType, String name, T stateValue) {
		super(stateType, name);

		setStateValue(stateValue);
	}

	/**
	 * Sets the state value.
	 * @param value the new state value
	 */
	public void setValue(T value) {
		setStateValue(value);
	}
}
package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * Abstract super type of {@code State} classes with two {@code State}
 * parameters of the same type.
 * 
 * @param <T> the type of the {@code State} value and its parameter
 *            {@code State} values
 */
public abstract class AbstractBinaryState<T> extends State<T> {

	private class TransitionHandler1 extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			updateValue(event.getNewValue(), paramState2.getValue());
			return true;
		}
	}

	private class TransitionHandler2 extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			updateValue(paramState1.getValue(), event.getNewValue());
			return true;
		}
	}

	private State<T> paramState1;
	private State<T> paramState2;
	private TransitionHandler1 transitionHandler1;
	private TransitionHandler2 transitionHandler2;

	/**
	 * Constructs an {@code AbstractBinaryState}.
	 * 
	 * @param stateType   the type of state values
	 * @param name        the state name
	 * @param paramState1 first state parameter
	 * @param paramState2 second state parameter
	 */
	protected AbstractBinaryState(Class<T> stateType, String name, State<T> paramState1, State<T> paramState2) {
		super(stateType, name);

		this.paramState1 = paramState1;
		this.paramState2 = paramState2;

		updateValue(paramState1.getValue(), paramState2.getValue());
		transitionHandler1 = new TransitionHandler1();
		transitionHandler2 = new TransitionHandler2();
		paramState1.registerTransitionListener(transitionHandler1);
		paramState2.registerTransitionListener(transitionHandler2);
	}

	/**
	 * Returns the {@code State} of the first parameter.
	 * 
	 * @return {@code State} of the first parameter
	 */
	public State<T> getParamState1() {
		return paramState1;
	}

	/**
	 * Sets the {@code State} of the first parameter.
	 * 
	 * @param paramState1 the {@code State} of the first parameter
	 */
	public void setParamState1(State<T> paramState1) {

		this.paramState1.unregisterTransitionListener(transitionHandler1);

		this.paramState1 = paramState1;
		updateValue(paramState1.getValue(), paramState2.getValue());

		paramState1.registerTransitionListener(transitionHandler1);
	}

	/**
	 * Returns the {@code State} of the second parameter.
	 * 
	 * @return {@code State} of the second parameter
	 */
	public State<T> getParamState2() {
		return paramState2;
	}

	/**
	 * Sets the {@code State} of the second parameter.
	 * 
	 * @param paramState2 the {@code State} of the second parameter
	 */
	public void setParamState2(State<T> paramState2) {

		this.paramState2.unregisterTransitionListener(transitionHandler2);

		this.paramState2 = paramState2;
		updateValue(paramState1.getValue(), paramState2.getValue());

		paramState2.registerTransitionListener(transitionHandler2);
	}

	protected abstract void updateValue(T paramValue1, T paramValue2);
}

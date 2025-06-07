package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * A {@code SwitchedState} has a primary and a secondary state while its own
 * state value can be the one or the other based on
 * {@link #setPrimaryStateSet(boolean)}.
 * 
 * @param <T> the type of the {@code State} value
 */
public class SwitchedState<T> extends State<T> {

	class SubTransitionHandler extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			setStateValue(event.getNewValue());
			return true;
		}
	}

	private State<T> primaryState;
	private State<T> secondaryState;
	private boolean primaryStateSet;

	/**
	 * Constructs a {@code SwitchedState}.
	 * 
	 * @param stateType      the type of state values
	 * @param name           the state name
	 * @param primaryState   the primary state
	 * @param secondaryState the secondary state
	 */
	public SwitchedState(Class<T> stateType, String name, State<T> primaryState, State<T> secondaryState) {
		super(stateType, name);

		this.primaryState = primaryState;
		this.secondaryState = secondaryState;

		secondaryState.registerTransitionListener(new SubTransitionHandler());

		primaryStateSet = true;
	}

	/**
	 * Constructs a {@code SwitchedState} while primary and secondary states are
	 * implicitly created of type {@code SimpleState}.
	 * 
	 * @param stateType the type of state values
	 * @param name      the state name
	 */
	public SwitchedState(Class<T> stateType, String name) {
		this(stateType, name, new SimpleState<T>(stateType, "primary " + name),
				new SimpleState<T>(stateType, "secondary " + name));
	}

	/**
	 * Returns the primary state.
	 * 
	 * @return the primary state
	 */
	public State<T> getPrimaryState() {
		return primaryState;
	}

	/**
	 * Returns the secondary state.
	 * 
	 * @return the secondary state
	 */
	public State<T> getSecondaryState() {
		return secondaryState;
	}

	/**
	 * Returns if the primary state is the exposed state. Otherwise the secondary
	 * state is the exposed state.
	 * 
	 * @return if the primary state is the exposed state
	 */
	public boolean isPrimaryStateSet() {
		return primaryStateSet;
	}

	/**
	 * Defines if the primary state is the exposed state.
	 * 
	 * @param doSet {@code true} if the primary state is the exposed state,
	 *              {@code false} if the secondary state is the exposed state.
	 */
	public void setPrimaryStateSet(boolean doSet) {

		if (primaryStateSet == doSet)
			return;

		primaryStateSet = doSet;

		if (primaryStateSet) {
			secondaryState.unregisterTransitionListener(new SubTransitionHandler());
			primaryState.registerTransitionListener(new SubTransitionHandler());

			setStateValue(primaryState.getValue());
		} else {
			primaryState.unregisterTransitionListener(new SubTransitionHandler());
			secondaryState.registerTransitionListener(new SubTransitionHandler());

			setStateValue(secondaryState.getValue());
		}
	}
}
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

	public SwitchedState(Class<T> stateType, String name, State<T> primaryState, State<T> secondaryState) {
		super(stateType, name);

		this.primaryState = primaryState;
		this.secondaryState = secondaryState;

		secondaryState.registerTransitionListener(new SubTransitionHandler());

		primaryStateSet = true;
	}

	public SwitchedState(Class<T> stateType, String name) {
		this(stateType, name, new SimpleState<T>(stateType, "primary " + name),
				new SimpleState<T>(stateType, "secondary " + name));
	}

	public State<T> getPrimaryState() {
		return primaryState;
	}

	public State<T> getSecondaryState() {
		return secondaryState;
	}

	public boolean isPrimaryStateSet() {
		return primaryStateSet;
	}

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
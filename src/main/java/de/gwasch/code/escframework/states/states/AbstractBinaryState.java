package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

public abstract class AbstractBinaryState<T> extends State<T> {

	class TransitionHandler1 extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			updateValue(event.getNewValue(), paramState2.getValue());
			return true;
		}
	}

	class TransitionHandler2 extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			updateValue(paramState1.getValue(), event.getNewValue());
			return true;
		}
	}
	
	private State<T> paramState1;
	private State<T> paramState2;
	public TransitionHandler1 transitionHandler1;
	public TransitionHandler2 transitionHandler2;

	public AbstractBinaryState(Class<T> stateType, String name, State<T> paramState1, State<T> paramState2) {
		super(stateType, name);

		this.paramState1 = paramState1;
		this.paramState2 = paramState2;

		updateValue(paramState1.getValue(), paramState2.getValue());
		transitionHandler1 = new TransitionHandler1();
		transitionHandler2 = new TransitionHandler2();
		paramState1.registerTransitionListener(transitionHandler1);
		paramState2.registerTransitionListener(transitionHandler2);
	}

	public State<T> getParamState1() {
		return paramState1;
	}

	public void setParamState1(State<T> paramState1) {

		this.paramState1.unregisterTransitionListener(transitionHandler1);

		this.paramState1 = paramState1;
		updateValue(paramState1.getValue(), paramState2.getValue());

		paramState1.registerTransitionListener(transitionHandler1);
	}

	public State<T> getParamState2() {
		return paramState2;
	}

	public void setParamState2(State<T> paramState2) {

		this.paramState2.unregisterTransitionListener(transitionHandler2);

		this.paramState2 = paramState2;
		updateValue(paramState1.getValue(), paramState2.getValue());

		paramState2.registerTransitionListener(transitionHandler2);
	}

	protected abstract void updateValue(T paramValue1, T paramValue2);
}

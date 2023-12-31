package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * {@code FunctionState} is parameterized by a {@link Function} and parameter
 * states. {@code FunctionState} registers for changes of the parameter states.
 * Every parameter change leads to an invocation of {@link Function#getValue()}.
 * Its return value is used to set the new state value.
 * <p>
 * It is assumed that a corresponding {@code Function} knows its state parameters on
 * its own. Though, it does not need to register for their transitions.
 * 
 * @param <T> the type of the {@code State} value
 * 
 * @see AggregateState
 */
public class FunctionState<T> extends State<T> {

	class ParamsTransitionHandler extends EventAdapter<TransitionEvent<?>> {

		public boolean onProcess(TransitionEvent<?> event) {

			if (function != null) {
				setStateValue(function.getValue());
			}

			return true;
		}
	}

	private Function<T> function;
	private ParamsTransitionHandler paramsTransitionHandler;

	public FunctionState(Class<T> stateType, String name) {
		super(stateType, name);

		function = null;
		paramsTransitionHandler = new ParamsTransitionHandler();
	}

	public void addParamState(State<?> state) {
		state.registerAnyTransitionListener(paramsTransitionHandler);
		paramsTransitionHandler.onProcess(null);
	}

	public void setFunction(Function<T> function) {
		this.function = function;
		paramsTransitionHandler.onProcess(null);
	}
}

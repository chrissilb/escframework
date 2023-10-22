package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.aggregations.Aggregation;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * {@code AggregateState} allows the application of an {@link Aggregation}. An
 * {@code AggregateState} has an arbitrary number of child values. The types of
 * child values and the result type must be the same.
 * 
 * @param <T> the type of the child values and result value
 * 
 */
public class AggregateState<T> extends State<T> {

	class TransitionHandler extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {
			aggregation.removeChildValue(event.getOldValue());
			aggregation.addChildValue(event.getNewValue());
			setStateValue(aggregation.getValue());
			return true;
		}
	}

	private Aggregation<T> aggregation;
	private TransitionHandler transitionHandler;

	/**
	 * Constructs an {@code AggregateState}.
	 * 
	 * @param stateType  the type of state values
	 * @param name       the state name
	 * @param aggregaton the aggregation which defines the algorithm to calculate
	 *                   the state value based on child state values
	 */
	public AggregateState(Class<T> stateType, String name, Aggregation<T> aggregaton) {
		super(stateType, name);
		this.aggregation = aggregaton;
		transitionHandler = new TransitionHandler();
	}

	public Aggregation<T> getAggregation() {
		return aggregation;
	}

	public void addChildState(State<T> state) {
		state.registerTransitionListener(transitionHandler);
		aggregation.addChildValue(state.getValue());
		setStateValue(aggregation.getValue());
	}

	public void removeChildState(State<T> state) {
		state.unregisterTransitionListener(transitionHandler);
		aggregation.removeChildValue(state.getValue());
		setStateValue(aggregation.getValue());
	}
}
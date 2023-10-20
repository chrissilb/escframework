package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.aggregations.Aggregation;
import de.gwasch.code.escframework.states.exceptions.LocalStateNotFoundException;

//todo, merkwürdige implementierung. erstmal ganz löschen?
public class ComboState<T> extends CompositeState<T> {

	private SwitchedState<T> rootState;

	private AggregateState<T> aggregateState;
	private SimpleState<T> localState = null;

	public ComboState(Class<T> stateType, String name, Aggregation<T> aggregaton) {
		super(stateType, name);

		SimpleState<T> primarystate = new SimpleState<T>(stateType, "primary " + name);
		aggregateState = new AggregateState<T>(stateType, "secondary " + name, aggregaton);
		rootState = new SwitchedState<T>(stateType, "switched " + name, primarystate, aggregateState);
	}

	public T getPrimaryValue() {
		return rootState.getPrimaryState().getValue();
	}

	public void setPrimaryValue(T value) {
		rootState.getPrimaryState().setValue(value);
	}

	public T getLocalValue() {

		if (localState != null)
			return localState.getValue();
		else
			throw new LocalStateNotFoundException();
	}

	public void setLocalValue(T value) {

		if (localState == null) {
			localState = new SimpleState<T>(getStateType(), "local " + getName());
			aggregateState.addChildState(localState);
		}
		localState.setValue(value);
	}

	public boolean isPrimaryStateSet() {
		return rootState.isPrimaryStateSet();
	}

	public void setPrimaryStateSet(boolean doset) {
		rootState.setPrimaryStateSet(doset);
	}

	protected State<T> getRootState() {
		return rootState;
	}

	public void addChildState(State<T> state) {
		aggregateState.addChildState(state);
	}

	public void removeChildState(State<T> state) {
		aggregateState.removeChildState(state);
	}
}
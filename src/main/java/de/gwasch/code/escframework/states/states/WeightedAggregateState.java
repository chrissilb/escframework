package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.aggregations.DoubleWeightedAverage;
import de.gwasch.code.escframework.states.events.TransitionEvent;

public class WeightedAggregateState extends State<Double> {

	class ChildTransitionHandler extends EventAdapter<TransitionEvent<Double>> {
		
		public boolean onProcess(TransitionEvent<Double> event) {
			aggregation.changeChildStateValue(event.getState(), event.getNewValue(), event.getOldValue());
	        setStateValue(aggregation.getValue());
	        return true;
		}
	}
	
    private DoubleWeightedAverage aggregation;
    ChildTransitionHandler transitionHandler;

    public WeightedAggregateState(String name) {
    	super(Double.class, name);

        aggregation = new DoubleWeightedAverage();
        transitionHandler = new ChildTransitionHandler();
    }


    public void AddChildState(State<Double> state, int weight)
    {
        state.registerTransitionListener(transitionHandler);
        aggregation.addChildState(state, weight);
        setStateValue(aggregation.getValue());
    }


    public void RemoveChildState(State<Double> state)
    {
        state.registerTransitionListener(transitionHandler);
        aggregation.removeChildState(state);
        setStateValue(aggregation.getValue());
    }


}
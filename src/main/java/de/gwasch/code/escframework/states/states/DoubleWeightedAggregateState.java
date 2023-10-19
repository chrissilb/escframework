package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.aggregations.DoubleWeightedAverageAggregation;
import de.gwasch.code.escframework.states.events.TransitionEvent;

public class DoubleWeightedAggregateState extends State<Double> {

	class TransitionHandler extends EventAdapter<TransitionEvent<Double>> {
		
		public boolean onProcess(TransitionEvent<Double> event) {
			aggregation.changeChildStateValue(event.getState(), event.getNewValue(), event.getOldValue());
	        setStateValue(aggregation.getValue());
	        return true;
		}
	}
	
    private DoubleWeightedAverageAggregation aggregation;
    private TransitionHandler transitionHandler;

    public DoubleWeightedAggregateState(String name) {
    	super(Double.class, name);

        aggregation = new DoubleWeightedAverageAggregation();
        transitionHandler = new TransitionHandler();
    }

    public void addChildState(State<Double> state, int weight) {
        state.registerTransitionListener(transitionHandler);
        aggregation.addChildState(state, weight);
        setStateValue(aggregation.getValue());
    }

    public void removeChildState(State<Double> state) {
        state.registerTransitionListener(transitionHandler);
        aggregation.removeChildState(state);
        setStateValue(aggregation.getValue());
    }


}
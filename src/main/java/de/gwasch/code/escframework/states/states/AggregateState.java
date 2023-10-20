package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.aggregations.Aggregation;
import de.gwasch.code.escframework.states.events.TransitionEvent;



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
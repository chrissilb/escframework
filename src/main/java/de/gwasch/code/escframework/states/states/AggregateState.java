package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.handler.EventAdapter;
import de.gwasch.code.escframework.states.aggregations.SimpleAggregation;
import de.gwasch.code.escframework.states.events.TransitionEvent;



public class AggregateState<T> extends State<T> {
	
	class ChildrenTransitionHandler extends EventAdapter<TransitionEvent<T>> {
		
		public boolean onProcess(TransitionEvent<T> event) {
			aggregation.removeChildState(event.getOldValue());
	        aggregation.addChildState(event.getNewValue());
	        setStateValue(aggregation.getValue());
	        return true;
		}
	}	
	
    private SimpleAggregation<T> aggregation;
    private ChildrenTransitionHandler childrenTransitionHandler;
  

    public AggregateState(Class<T> stateType, String name, SimpleAggregation<T> aggregaton) {
    	super(stateType, name);
        this.aggregation = aggregaton;
        childrenTransitionHandler = new ChildrenTransitionHandler();
    }
 

    public SimpleAggregation<T> getAggregation() {
        return aggregation;
    }


    public void addChildState(State<T> state) {
    	
        state.registerTransitionListener(childrenTransitionHandler);
        aggregation.addChildState(state.getValue());
        setStateValue(aggregation.getValue());
    }


    public void removeChildState(State<T> state) {
    	
        state.unregisterTransitionListener(childrenTransitionHandler);
        aggregation.removeChildState(state.getValue());
        setStateValue(aggregation.getValue());
    }
}
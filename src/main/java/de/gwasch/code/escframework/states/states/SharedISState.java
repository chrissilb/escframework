package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.aggregations.Aggregation;

public class SharedISState<T extends Enum<T>> extends AbstractISState<T> {

  

    public SharedISState(Class<T> stateType, String name, Aggregation<T> aggregation) {
        super(stateType, name, new AggregateState<T>(stateType, "shared strived " + name, aggregation));
    
    }
   
    public void addStrivedState(State<T> state) {
        AggregateState<T> aggrstrivedstate = (AggregateState <T>)rootState.getStrivedState();
        aggrstrivedstate.addChildState(state);
    }


    public void removeStrivedState(State<T> state) {
        AggregateState<T> aggrstrivedstate = (AggregateState<T>)rootState.getStrivedState();
        aggrstrivedstate.removeChildState(state);
    }

}
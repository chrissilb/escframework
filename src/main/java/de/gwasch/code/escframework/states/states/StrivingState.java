package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;


enum RestrictionType { Offensive, Defensive }


public class StrivingState<T extends Enum<T>> extends State<T> {
	
	class OnStrivedTransition extends EventAdapter<TransitionEvent<T>> {
		
		public boolean onProcess(TransitionEvent<T> event) {
			
			if      (gt(strivedState, StrivingState.this)) doTransition(startRestrictiveState);
            else if (lt(strivedState, StrivingState.this)) doTransition(stopRestrictiveState); 
			
			return true;
		}
	}

	class OnStartRestrictionTransition extends EventAdapter<TransitionEvent<T>> {
		
		public boolean onProcess(TransitionEvent<T> event) {
			
	        if (gt(event.getNewValue(), event.getOldValue())) {
 	            if (gt(startRestrictiveState, StrivingState.this)) doTransition(startRestrictiveState);
 	        }
 	        else if (lt(event.getNewValue(), event.getOldValue()) && restrictionType == RestrictionType.Offensive) {
 	            if (lt(startRestrictiveState, StrivingState.this)) doTransition(startRestrictiveState);
 	        }
	        
	        return true;
		}
	}

	class OnStopRestrictionTransition extends EventAdapter<TransitionEvent<T>> {
		
		public boolean onProcess(TransitionEvent<T> event) {
			
	        if (lt(event.getNewValue(), event.getOldValue())) {
	            if (lt(stopRestrictiveState, StrivingState.this)) doTransition(stopRestrictiveState);
	        }
	        else if (gt(event.getNewValue(), event.getOldValue()) && restrictionType == RestrictionType.Offensive) {
	            if (gt(stopRestrictiveState, StrivingState.this)) doTransition(stopRestrictiveState);
	        }		
	        
	        return true;
		}
	}
	
	
    private RestrictionType restrictionType;

    private State<T> strivedState;
    private State<T> startRestrictiveState;
    private State<T> stopRestrictiveState;


    public StrivingState(Class<T> stateType, String name, RestrictionType restrictionType, State<T> strivedState) {
    	super(stateType, name);
    	
    	if (restrictionType == RestrictionType.Defensive) {
    		setName("Def:" + getName());
    	}
    	else {
    		setName("Off:" + getName());
    	}
    	
        this.restrictionType = restrictionType;

        this.strivedState = strivedState;
        strivedState.registerTransitionListener(new OnStrivedTransition());
    }


    public StrivingState(Class<T> stateType, String name, RestrictionType restrictionType) {
     	this(stateType, name, restrictionType, new SimpleState<T>(stateType, name));
    }

    public StrivingState(Class<T> stateType, String name, State<T> strivedState) {
		this(stateType, name, RestrictionType.Defensive, strivedState);
    }

    public RestrictionType getRestrictionType() {
    	return restrictionType;
    }

    public State<T> getStartRestriction() {
        return startRestrictiveState; 
    }
    
    public void setStartRestriction(State<T> restriction) {
 
	    if (startRestrictiveState == null) {
	    	restriction.registerTransitionListener(new OnStartRestrictionTransition());
	    }
	    
	    startRestrictiveState = restriction;
    }

    public State<T> getStopRestriction() {
    	return stopRestrictiveState; 
    }
    
    public void setStopRestriction(State<T> restriction) {
 
	    if (stopRestrictiveState == null) {
	    	restriction.registerTransitionListener(new OnStopRestrictionTransition());
	    }
	    
	    stopRestrictiveState = restriction;
    }

    public void setValue(T value) {
    	
        if (strivedState.getValue().equals(value) && !getValue().equals(value) 
            && (restrictionType == RestrictionType.Offensive || getActivityHandler() != null)) {
        	
            strivedState.fireTransitionEvents();
        }
        else {
            strivedState.setValue(value); 
        }
    }


    public State<T> getStrivedState() {
    	return strivedState;
    }  
    
    private void doTransition(State<T> depstate) {
        
    	if (depstate != null) {
            if (   (restrictionType == RestrictionType.Defensive && depstate == startRestrictiveState)
                || (restrictionType == RestrictionType.Offensive && depstate == stopRestrictiveState)) {
            	
            	setStateValue(min(depstate, strivedState));
            } 
            else {
            	setStateValue(max(depstate, strivedState));
            }
        }
        else {
            setStateValue(strivedState.getValue());
        }
    }

 
    private T min(State<T> state1, State<T> state2) {
    	State<T> state = (lt(state1, state2)) ? state1 : state2;
    	return state.getValue();
    }

    private T max(State<T> state1, State<T> state2) {
    	State<T> state = (gt(state1, state2)) ? state1 : state2;
    	return state.getValue();
    }

    private boolean gt(T value1, T value2) {
        return value1.compareTo(value2) > 0;
    }

    private boolean lt(T value1, T value2) {
        return value1.compareTo(value2) < 0;
    }
    
    private boolean gt(State<T> state1, State<T> state2) {
        return gt(state1.getValue(), state2.getValue());
    }

    private boolean lt(State<T> state1, State<T> state2) {
        return lt(state1.getValue(), state2.getValue());
    }
}
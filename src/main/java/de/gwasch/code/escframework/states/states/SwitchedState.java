package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

public class SwitchedState<T> extends State<T> {

	class SubTransitionHandler extends EventAdapter<TransitionEvent<T>> {
		
		public boolean onProcess(TransitionEvent<T> event) {
			setStateValue(event.getNewValue());
			return true;
		}
	}
	
	
    private State<T> primaryState;
    private State<T> secondaryState;
    private boolean primaryStateSet;

    public SwitchedState(Class<T> stateType, String name, State<T> primary, State<T> secondary) {
    	super(stateType, name);
    	
        primaryState = primary; 
        secondaryState = secondary;

        secondaryState.registerTransitionListener(new SubTransitionHandler());
        
        primaryStateSet = true; 
    }


    public SwitchedState(Class<T> stateType, String name) {
        this(stateType, name, new SimpleState<T>(stateType, "primary " + name), new SimpleState<T>(stateType, "secondary " + name));
    }

    public State<T> getPrimaryState() {
		return primaryState; 
    }

    public State<T> getSecondaryState() {
        return secondaryState; 
    }
    
    public boolean isPrimaryStateSet() {
    	return primaryStateSet; 
    }
    
    public void setPrimaryStateSet(boolean doset) {

        if (primaryStateSet == doset) return;

        primaryStateSet = doset;

        if (primaryStateSet) {
            secondaryState.unregisterTransitionListener(new SubTransitionHandler());
            primaryState.registerTransitionListener(new SubTransitionHandler());

            setStateValue(primaryState.getValue());
        }
        else {
            primaryState.unregisterTransitionListener(new SubTransitionHandler());
            secondaryState.registerTransitionListener(new SubTransitionHandler());

            setStateValue(secondaryState.getValue());
        }
    }
}
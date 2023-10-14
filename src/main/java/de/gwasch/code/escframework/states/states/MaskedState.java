package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.utils.StateMask;

public class MaskedState <T, U> extends State<T> {

	class TransitionHandler extends EventAdapter<TransitionEvent<U>> {
		
		public boolean onProcess(TransitionEvent<U> event) {
			setStateValue(stateMask.getMaskState(event.getNewValue()));
			return true;
		}
	}
	
    private State<U> nativeState;
    private StateMask<T, U> stateMask;

    public TransitionHandler transitionHandler;
    
    public MaskedState(Class<T> stateType, State<U> state, StateMask<T, U> statemask) {
    	super(stateType, "masked " + state.getName());
    
        nativeState = state;
        stateMask = statemask;

        transitionHandler = new TransitionHandler();
        setStateValue(stateMask.getMaskState(nativeState.getValue()));
        nativeState.registerTransitionListener(transitionHandler);
    }

   
    public State<U> getNativeState() {
    	return nativeState; 
    }
    
    public void setNativeState(State<U> state) {

        nativeState.unregisterTransitionListener(transitionHandler);
        
        nativeState = state;
        setStateValue(stateMask.getMaskState(nativeState.getValue()));

        nativeState.registerTransitionListener(transitionHandler);
    }
}

package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * {@code AbstractConversionState} is the common super type for classes which convert from one state type to another state type.
 *  
 * @param <T> the target state type
 * @param <U> the source state type
 */
public abstract class AbstractConversionState<T, U> extends State<T> {

	class TransitionHandler extends EventAdapter<TransitionEvent<U>> {
		
		public boolean onProcess(TransitionEvent<U> event) {
			updateValue(event.getNewValue());
			
			return true;
		}
	}
	
    private State<U> paramState;
    private TransitionHandler transitionHandler;
    
    public AbstractConversionState(Class<T> stateType, String name, State<U> paramState) {
    	super(stateType, name);
    
        this.paramState = paramState;

        transitionHandler = new TransitionHandler();
        paramState.registerTransitionListener(transitionHandler);
    }

   
    public State<U> getParamState() {
    	return paramState; 
    }
    
    public void setParamState(State<U> paramState) {

        this.paramState.unregisterTransitionListener(transitionHandler);
        
        this.paramState = paramState;
        updateValue(paramState.getValue());

        paramState.registerTransitionListener(transitionHandler);
    }
    
    protected abstract void updateValue(U paramValue);
}

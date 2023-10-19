package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.utils.Mask;

public class MaskedState <T, U> extends AbstractConversionState<T, U> {

    private Mask<T, U> stateMask;
    
    public MaskedState(Class<T> stateType, String name, State<U> paramState, Mask<T, U> stateMask) {
    	super(stateType, name, paramState);
    
    	this.stateMask = stateMask;
    	updateValue(paramState.getValue());
    }
    
    protected void updateValue(U paramValue) {
    	
    	setStateValue(stateMask.getMaskedValue(paramValue));
    }
}

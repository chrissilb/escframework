package de.gwasch.code.escframework.states.utils;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.exceptions.StateMaskNotFoundException;

public class StateMask<T, U>
{
    private Map<U, T> maskStates;
    
    public StateMask() {
    	maskStates = new HashMap<U, T>();
    }

    public T getMaskState(U realstate) {
    	T maskstate;
    	
    	maskstate = maskStates.get(realstate);
    	
        if (maskstate == null) {
            throw new StateMaskNotFoundException(realstate);
        }
        
        return maskstate;
    }
    
    public void setMaskState(U realstate, T maskstate) { 
    	maskStates.put(realstate, maskstate);
    }
}
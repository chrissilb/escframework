package de.gwasch.code.escframework.states.utils;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.exceptions.StateMaskNotFoundException;

/**
 * A {@code Mask} converts values ({@code actualValue}) to other values ({@code maskedValue}).
 * 
 * @param <T> the actual input value type
 * @param <U> the masked output value type
 */
public class Mask<T, U>
{
    private Map<U, T> maskedValues;
    
    /**
     * Constructs a {@code Mask}.
     */
    public Mask() {
    	maskedValues = new HashMap<U, T>();
    }

    /**
     * Returns a masked value based on an actual value.
     * @param actualValue the actual value
     * @return the masked value
     */
    public T getMaskedValue(U actualValue) {
    	
    	T maskedValue;
    	
    	maskedValue = maskedValues.get(actualValue);
    	
        if (maskedValue == null) {
            throw new StateMaskNotFoundException(actualValue);
        }
        
        return maskedValue;
    }
    
    /**
     * Sets a pair of actual value and its corresponding masked value
     *  
     * @param actualValue the actual value
     * @param maskedValue the masked value
     */
    public void setMaskedValue(U actualValue, T maskedValue) { 
    	maskedValues.put(actualValue, maskedValue);
    }
}
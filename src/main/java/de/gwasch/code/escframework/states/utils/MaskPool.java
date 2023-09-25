package de.gwasch.code.escframework.states.utils;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.exceptions.MaskAlreadyDefinedException;

public class MaskPool
{
    private static MaskPool theInstance = null;
    Map<Class<?>, MaskSet<?>> maskPool;

    private MaskPool() { 
    	maskPool = new HashMap<Class<?>, MaskSet<?>>();
    }

    public static MaskPool getInstance() {
        if (theInstance == null) theInstance = new MaskPool();
        return theInstance;
    }

    public void addMaskSet(Class<?> type) {
        if (maskPool.containsKey(type)) {
            throw new MaskAlreadyDefinedException(type);
        }
        
        maskPool.put(type, null);
    }


    public <T extends Enum<T>> void addMaskSet(Class<T> type, T integrativeState) {
        if (maskPool.containsKey(type)) {
            throw new MaskAlreadyDefinedException(type);
        }
        
        StateMask<T, T> startMask = new StateMask<T, T>();
        StateMask<T, T> stopMask = new StateMask<T, T>();

        T[] stateValues = type.getEnumConstants();
        T firstState = stateValues[0];
        T laststate = stateValues[stateValues.length - 1];
        T beforeIntegrative;

        int idx = integrativeState.ordinal();

        if (integrativeState.equals(firstState)) {
            beforeIntegrative = integrativeState;		
        }
        else {
            beforeIntegrative = stateValues[idx - 1];
        }

        for (T s : stateValues) {
        	
            if (s.compareTo(integrativeState) < 0) {
                startMask.setMaskState(s, beforeIntegrative);
                stopMask.setMaskState(s, firstState);
            }
            else {
                startMask.setMaskState(s, laststate);
                stopMask.setMaskState(s, integrativeState);
            }
        }

        maskPool.put(type, new MaskSet<T>(startMask, stopMask));
    }


    public <T> MaskSet<T> getMaskSet(Class<T> type) 
    {
		@SuppressWarnings("unchecked")
    	MaskSet<T> maskSet = (MaskSet<T>)maskPool.get(type);
		return maskSet;
    }


    public boolean hasMaskSet(Class<?> type) {
        return maskPool.containsKey(type);
    }
}
package de.gwasch.code.escframework.states.utils;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.exceptions.MaskAlreadyDefinedException;

public class MaskPool
{
    private static MaskPool theInstance = null;
    Map<Class<?>, MaskPair<?>> maskPool;

    private MaskPool() { 
    	maskPool = new HashMap<Class<?>, MaskPair<?>>();
    }

    public static MaskPool getInstance() {
        if (theInstance == null) theInstance = new MaskPool();
        return theInstance;
    }

    public void addMaskPair(Class<?> type) {
        if (maskPool.containsKey(type)) {
            throw new MaskAlreadyDefinedException(type);
        }
        
        maskPool.put(type, null);
    }


    public <T extends Enum<T>> void addMaskPair(Class<T> type, T integrativeState) {
        if (maskPool.containsKey(type)) {
            throw new MaskAlreadyDefinedException(type);
        }
        
        Mask<T, T> startMask = new Mask<T, T>();
        Mask<T, T> stopMask = new Mask<T, T>();

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
                startMask.setMaskedValue(s, beforeIntegrative);
                stopMask.setMaskedValue(s, firstState);
            }
            else {
                startMask.setMaskedValue(s, laststate);
                stopMask.setMaskedValue(s, integrativeState);
            }
        }

        maskPool.put(type, new MaskPair<T>(startMask, stopMask));
    }


    public <T> MaskPair<T> getMaskPair(Class<T> type) 
    {
		@SuppressWarnings("unchecked")
    	MaskPair<T> maskPair = (MaskPair<T>)maskPool.get(type);
		return maskPair;
    }


    public boolean hasMaskPair(Class<?> type) {
        return maskPool.containsKey(type);
    }
}
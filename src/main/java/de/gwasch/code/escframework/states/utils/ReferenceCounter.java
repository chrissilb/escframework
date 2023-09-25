package de.gwasch.code.escframework.states.utils;

import java.util.HashMap;
import java.util.Map;

public class ReferenceCounter<T> {


    private Map<T, Integer> referenceCounts;

    
    public ReferenceCounter() {
    	referenceCounts = new HashMap<T, Integer>();
    }
    
    
    public Map<T, Integer> getReferenceCounts()
    {
        return referenceCounts;
    }


    
    public void addReference(T reference)
    {
        increment(reference, 1);
    }


    // param force: we allow negative references, too
    // return: true - remove works, false - remove does not work
    public boolean removeReference(T reference, boolean force)
    {
        if (force || hasReference(reference))
        {
            increment(reference, -1);
            return true;
        }
        return false;
    }


    // return: true - remove works, false - remove does not work
    public boolean removeReference(T reference)
    {
        return removeReference(reference, false);
    }


    public boolean hasReference(T reference)
    {
        return (getReferenceCount(reference) > 0);
    }


    public int getReferenceCount(T reference)
    {
        if (!referenceCounts.containsKey(reference)) {
        	return 0;
        }
        
        return referenceCounts.get(reference);
    }


    private void increment(T reference, int delta)
    {
        int count = getReferenceCount(reference);
        count += delta;
        referenceCounts.put(reference, count);
    }
}

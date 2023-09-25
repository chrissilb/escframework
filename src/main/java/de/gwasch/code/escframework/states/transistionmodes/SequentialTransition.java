package de.gwasch.code.escframework.states.transistionmodes;

import de.gwasch.code.escframework.states.utils.EnumUtil;

public class SequentialTransition<T extends Enum<T>> implements TransitionMode<T>
{
    public T singleTransition(T target, T current)
    {
        int r = current.compareTo(target); 

        if (r < 0) {
        	return EnumUtil.increment(current);
        }
        
        if (r > 0) {
        	return EnumUtil.decrement(current);
        }
        
        return current;
    }
}

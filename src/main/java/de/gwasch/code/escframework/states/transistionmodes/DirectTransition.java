package de.gwasch.code.escframework.states.transistionmodes;

public class DirectTransition<T> implements TransitionMode<T> {

	public T singleTransition(T target, T current)
    {
        return target;
    }
}

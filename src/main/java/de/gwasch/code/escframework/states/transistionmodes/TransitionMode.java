package de.gwasch.code.escframework.states.transistionmodes;

public interface TransitionMode<T> {

	T singleTransition(T target, T current);
}

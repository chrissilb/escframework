package de.gwasch.code.escframework.states.events;

import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.states.states.State;


public class TransitionEvent<T> extends AbstractEvent {
    
	private State<T> state;
	private T newValue;
	private T oldValue;
	
	public TransitionEvent() {
		
	}
	
	public TransitionEvent(State<T> state, T newvalue, T oldvalue)
	{
		setSource(state);
	    this.state = state;
	    newValue = newvalue;
	    oldValue = oldvalue;
	}
	
	public TransitionEvent<T> clone() {
		TransitionEvent<T> clone = new TransitionEvent<>(state, newValue, oldValue);
		return clone;
	}
	
	public State<T> getState() {
		return state;
	}
	
	public T getNewValue() {
		return newValue;
	}
	
	public T getOldValue() {
		return oldValue;
	}
	
	public String toString() {
		return state.toString();
	}
}
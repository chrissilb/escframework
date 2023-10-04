package de.gwasch.code.escframework.states.states;

/**
 * {@code SimpleState} contains a single value which can be set directly.
 * 
 * @param <T> the type of the {@code State} value
 */
public class SimpleState<T> extends State<T> {
 
    public SimpleState(Class<T> stateType, String name) {    
    	super(stateType, name);
    }

    public void setValue(T value) {
        setStateValue(value);
    }
}
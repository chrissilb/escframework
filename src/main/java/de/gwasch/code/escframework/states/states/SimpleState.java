package de.gwasch.code.escframework.states.states;

public class SimpleState<T> extends State<T> {
 
    public SimpleState(Class<T> stateType, String name) {    
    	super(stateType, name);
    }
    

    public void setValue(T value) {
        setStateValue(value);
    }
}
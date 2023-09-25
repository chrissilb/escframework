package de.gwasch.code.escframework.states.states;

public class ISState<T extends Enum<T>> extends AbstractISState<T>
{
    public ISState(Class<T> stateType, String name) {
        super(stateType, name, new SimpleState<T>(stateType, name));
    
    }
}
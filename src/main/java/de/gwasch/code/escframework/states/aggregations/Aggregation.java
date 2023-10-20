package de.gwasch.code.escframework.states.aggregations;

public abstract class Aggregation<T> {

	protected Aggregation() {
	}
	
    public abstract void addChildValue(T value);
    public abstract void removeChildValue(T value);    
	public abstract T getValue();
}

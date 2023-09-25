package de.gwasch.code.escframework.states.aggregations;

public interface SimpleAggregation<T> extends Aggregation<T> {
	
    void addChildState(T state);
    void removeChildState(T state);
}

package de.gwasch.code.escframework.states.aggregations;

public interface SimpleAggregation<T> extends Aggregation<T> {
	
    void addChildValue(T value);
    void removeChildValue(T value);
}

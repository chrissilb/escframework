package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.states.AggregateState;

/**
 * An {@code Aggregation} provides an aggregate function. It is meant to be used
 * in the context of an {@link AggregateState}. An {@code Aggregation} has an
 * arbitrary number of child values. The type of child values and the result
 * type must be the same.
 * 
 * @param <T> the type of the child values and result value
 */
public abstract class Aggregation<T> {

	protected Aggregation() {
	}

	public abstract void addChildValue(T value);

	public abstract void removeChildValue(T value);

	public abstract T getValue();
}

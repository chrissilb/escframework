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
public interface Aggregation<T> {

	/**
	 * Adds a child value.
	 * @param childValue the child value
	 */
	void addChildValue(T childValue);

	/**
	 * Removes a child value.
	 * @param childValue the child value
	 */
	void removeChildValue(T childValue);

	/**
	 * Returns the aggregated value.
	 * @return the aggregated value
	 */
	T getValue();
}

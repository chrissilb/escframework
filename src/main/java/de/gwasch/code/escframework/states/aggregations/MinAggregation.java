package de.gwasch.code.escframework.states.aggregations;

import java.util.Collections;

/**
 * {@code MinAggregation} provides the minimum of child values.
 * 
 * @param <T> the type of the child values and result value
 */
public class MinAggregation<T extends Comparable<T>> extends ComparableAggregation<T> {

	public MinAggregation() {
	}

	public T getValue() {
		return Collections.min(getList());
	}
}

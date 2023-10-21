package de.gwasch.code.escframework.states.aggregations;

import java.util.Collections;

/**
 * {@code MaxAggregation} provides the maximum of child values.
 * 
 * @param <T> the type of the child values and result value
 */

public class MaxAggregation<T extends Comparable<T>> extends ComparableAggregation<T> {

	public MaxAggregation() {
	}

	public T getValue() {
		return Collections.max(getList());
	}
}

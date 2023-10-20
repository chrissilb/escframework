package de.gwasch.code.escframework.states.aggregations;

import java.util.Collections;

public class MaxAggregation<T extends Comparable<T>> extends ComparableAggregation<T> {

	public MaxAggregation() {
	}

	public T getValue() {
		return Collections.max(getList());
	}
}

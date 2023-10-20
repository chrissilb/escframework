package de.gwasch.code.escframework.states.aggregations;

import java.util.Collections;

public class MinAggregation<T extends Comparable<T>> extends ComparableAggregation<T> {

	public MinAggregation() {
	}

	public T getValue() {
		return Collections.min(getList());
	}
}

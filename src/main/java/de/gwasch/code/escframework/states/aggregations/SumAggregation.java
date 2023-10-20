package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.utils.TypeUtil;

public abstract class SumAggregation<T extends Number & Comparable<T>> extends Aggregation<T> {

	private Class<T> stateType;
	private T sum;

	public SumAggregation(Class<T> stateType) {

		this.stateType = stateType;
		sum = TypeUtil.getDefaultValue(stateType);
	}

	public void addChildValue(T value) {

		sum = TypeUtil.add(stateType, sum, value);
	}

	public void removeChildValue(T value) {
		sum = TypeUtil.substract(stateType, sum, value);
	}

	public T getValue() {
		return sum;
	}
}

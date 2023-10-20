package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.utils.TypeUtil;

public class AvgAggregation<T extends Number & Comparable<T>> extends Aggregation<T> {
	
	private Class<T> stateType;
	private T sum;
	private int count;

	public AvgAggregation(Class<T> stateType) {

		this.stateType = stateType;
		sum = TypeUtil.getDefaultValue(stateType);
		count = 0;
	}

	public void addChildValue(T value) {

		sum = TypeUtil.add(stateType, sum, value);
		count++;

	}

	public void removeChildValue(T value) {

		sum = TypeUtil.substract(stateType, sum, value);
		count--;

	}

	public T getValue() {
		return (count != 0) ? TypeUtil.divide(stateType, sum, TypeUtil.cast(stateType, count))
				: TypeUtil.getDefaultValue(stateType);

	}
}

package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.utils.TypeUtil;

/**
 * {@code SumAggregation} provides the sum of child values.
 * <p>
 * Supported Number types are Integer, Double, Long and Float. Not supported are Short
 * and Byte because their arithmetic operations return int values.
 * 
 * @param <T> the type of the child values and result value
 */
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

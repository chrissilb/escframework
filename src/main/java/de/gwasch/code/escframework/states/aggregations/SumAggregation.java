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
public abstract class SumAggregation<T extends Number & Comparable<T>> implements Aggregation<T> {

	private Class<T> type;
	private T sum;

	/**
	 * Constructs a {@code SumAggregation}.
	 * @param type the type of the child values and result value
	 */
	public SumAggregation(Class<T> type) {

		this.type = type;
		sum = TypeUtil.getDefaultValue(type);
	}

	/**
	 * Adds a child value.
	 * @param childValue the child value
	 */
	public void addChildValue(T childValue) {

		sum = TypeUtil.add(type, sum, childValue);
	}

	/**
	 * Removes a child value.
	 * @param childValue the child value
	 */
	public void removeChildValue(T childValue) {
		sum = TypeUtil.substract(type, sum, childValue);
	}

	/**
	 * Returns the sum of child values.
	 * @return the sum of child values
	 */
	public T getValue() {
		return sum;
	}
}

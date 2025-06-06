package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.utils.TypeUtil;

/**
 * {@code AvgAggregation} provides the arithmetic average of child values.
 * <p>
 * Supported Number types are Integer, Double, Long and Float. Not supported are Short
 * and Byte because their arithmetic operations return int values.
 * 
 *  @param <T> the type of the child values and result value
 */
public class AvgAggregation<T extends Number & Comparable<T>> implements Aggregation<T> {

	private Class<T> type;
	private T sum;
	private int count;

	/**
	 * Constructs an {@code AvgAggregation}.
	 * @param type the type of the child values and result value
	 */
	public AvgAggregation(Class<T> type) {

		this.type = type;
		sum = TypeUtil.getDefaultValue(type);
		count = 0;
	}

	/**
	 * Adds a child value.
	 * @param childValue the child value
	 */
	public void addChildValue(T childValue) {

		sum = TypeUtil.add(type, sum, childValue);
		count++;
	}

	/**
	 * Removes a child value.
	 * @param childValue the child value
	 */
	public void removeChildValue(T childValue) {

		sum = TypeUtil.substract(type, sum, childValue);
		count--;

	}

	/**
	 * Returns the arithmetic average of child values.
	 * @return the arithmetic average of child values
	 */
	public T getValue() {
		return (count != 0) ? TypeUtil.divide(type, sum, TypeUtil.cast(type, count))
				: TypeUtil.getDefaultValue(type);

	}
}

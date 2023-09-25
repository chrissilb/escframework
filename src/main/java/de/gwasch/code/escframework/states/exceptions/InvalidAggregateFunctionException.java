package de.gwasch.code.escframework.states.exceptions;

import de.gwasch.code.escframework.states.aggregations.AggregateFunction;

public class InvalidAggregateFunctionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidAggregateFunctionException(AggregateFunction function) {
		super("Aggregate function '" + function + "' is not valid in this context!");
	}
}

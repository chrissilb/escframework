package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.exceptions.InvalidAggregateFunctionException;


public class ComparableAggregation<T extends Comparable<T>> extends AbstractComparableAggregation<T> {
 
	public ComparableAggregation(AggregateFunction function) {
		super(function);
		
        if (function != AggregateFunction.Min && function != AggregateFunction.Max)
            throw new InvalidAggregateFunctionException(function);
    }
}
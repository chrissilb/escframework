package de.gwasch.code.escframework.states.aggregations;


public class DoubleAggregation extends AbstractComparableAggregation<Double> {

    private double sum;            // NOTE: only used for Avg
    private int count;             // NOTE: only used for Avg

    public DoubleAggregation(AggregateFunction function) {
    	super(function);
    	
    	sum = 0.0;
    	count = 0;
    }

    public void addChildValue(Double value)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum += value;
            count++;
        }
        else {
            super.addChildValue(value);
        }
    }


    public void removeChildValue(Double value)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum -= value;
            count--;
       }
        else {
            super.removeChildValue(value);
        }
    }


    public Double getValue() {
    	
        if (aggregateFunction == AggregateFunction.Avg) {
        	 return (count != 0) ? sum / count : 0.0;
        }
        else {
        	return super.getValue();
        }   
    }
}


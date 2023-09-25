package de.gwasch.code.escframework.states.aggregations;


public class DoubleAggregation extends AbstractComparableAggregation<Double> {

    private double sum = 0;            // NOTE: only used for Avg
    private int count = 0;             // NOTE: only used for Avg

    public DoubleAggregation(AggregateFunction function) {
    	super(function);
    }

    public void addChildState(Double addstate)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum += addstate;
            count++;
        }
        else {
            super.addChildState(addstate);
        }
    }


    public void removeChildState(Double removestate)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum -= removestate;
            count--;
       }
        else {
            super.removeChildState(removestate);
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


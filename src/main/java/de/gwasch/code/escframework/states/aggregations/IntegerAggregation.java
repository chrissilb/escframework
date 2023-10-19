package de.gwasch.code.escframework.states.aggregations;


public class IntegerAggregation extends AbstractComparableAggregation<Integer> {

    private int sum;              // NOTE: only used for Avg
    private int count;            // NOTE: only used for Avg
   
    public IntegerAggregation(AggregateFunction function) {
    	super(function);
    	
    	sum = 0;
    	count = 0;
    }

    public void addChildValue(int value)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum += value; 
            count++;
        }
        else 
            super.addChildValue(value);
    }


    public void removeChildValue(int value)
    {
        if (aggregateFunction == AggregateFunction.Avg) {
            sum -= value; 
            count--;
        }
        else {
            super.removeChildValue(value);
        }
    }


    public Integer getValue() {
       
        if (aggregateFunction == AggregateFunction.Avg) {
            return (count != 0) ? sum / count : 0;
        }
        else {
            return super.getValue();
        }
    }
}
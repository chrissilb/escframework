package de.gwasch.code.escframework.states.aggregations;


public class IntegerAggregation extends AbstractComparableAggregation<Integer> {

    private int sum = 0;              // NOTE: only used for Avg
    private int count = 0;            // NOTE: only used for Avg
   
    public IntegerAggregation(AggregateFunction function) {
    	super(function);
    }

    public void addChildState(int addstate)
    {
        if (aggregateFunction == AggregateFunction.Avg)
        {
            sum += addstate; 
            count++;
        }
        else 
            super.addChildState(addstate);
    }


    public void removeChildState(int removestate)
    {
        if (aggregateFunction == AggregateFunction.Avg) {
            sum -= removestate; 
            count--;
        }
        else {
            super.removeChildState(removestate);
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
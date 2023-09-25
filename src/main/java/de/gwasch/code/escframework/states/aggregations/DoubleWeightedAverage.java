package de.gwasch.code.escframework.states.aggregations;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.states.State;

public class DoubleWeightedAverage implements Aggregation<Double> {
	
    private Map<State<Double>, Integer> weightList;

    private double sum = 0;                            
    private int count = 0;                              



    public DoubleWeightedAverage() {
    	weightList = new HashMap<State<Double>, Integer>();
    }

   

    public void addChildState(State<Double> addstate, int weight)
    {
        weightList.put(addstate, weight);

        sum += (addstate.getValue() * weight);
        count += weight;
    }


    public void removeChildState(State<Double> removestate)
    {
        int weight = weightList.get(removestate);
        sum -= (removestate.getValue() * weight);
        count -= weight;

        weightList.remove(removestate);
    }


    public void changeChildStateValue(State<Double> state, Double newvalue, Double oldvalue)
    {
        sum = sum + (newvalue - oldvalue) * weightList.get(state);
    }


    public Double getValue() {

    	return (count != 0) ? sum / count : 0.0;
    }
}

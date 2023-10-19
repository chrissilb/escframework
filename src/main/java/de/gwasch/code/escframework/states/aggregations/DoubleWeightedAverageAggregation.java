package de.gwasch.code.escframework.states.aggregations;

import java.util.HashMap;
import java.util.Map;

import de.gwasch.code.escframework.states.states.State;

public class DoubleWeightedAverageAggregation implements Aggregation<Double> {
	
    private Map<State<Double>, Integer> weightList;

    private double sum;                            
    private int count;                              

    public DoubleWeightedAverageAggregation() {
    	weightList = new HashMap<State<Double>, Integer>();
    	
    	sum = 0.0;
    	count = 0;
    }

    public void addChildState(State<Double> state, int weight) {
        weightList.put(state, weight);

        sum += (state.getValue() * weight);
        count += weight;
    }


    public void removeChildState(State<Double> state) {
        int weight = weightList.get(state);
        sum -= (state.getValue() * weight);
        count -= weight;

        weightList.remove(state);
    }


    public void changeChildStateValue(State<Double> state, Double newValue, Double oldValue) {
        sum = sum + (newValue - oldValue) * weightList.get(state);
    }


    public Double getValue() {
    	return (count != 0) ? sum / count : 0.0;
    }
}

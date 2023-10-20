package de.gwasch.code.escframework.states.aggregations;

import java.util.ArrayList;
import java.util.List;


public abstract class ComparableAggregation<T extends Comparable<T>> extends Aggregation<T> {
 
	private List<T> list;

	protected ComparableAggregation() {
		list = new ArrayList<T>();
    }
	
	protected List<T> getList() {
		return list;
	}
	
	public void addChildValue(T value) {
		list.add(value);
	}

	public void removeChildValue(T value) {
		list.remove(value);
	}
}
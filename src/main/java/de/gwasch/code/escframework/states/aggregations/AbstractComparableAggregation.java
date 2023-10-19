package de.gwasch.code.escframework.states.aggregations;

import de.gwasch.code.escframework.states.utils.ReferenceCounter;

public abstract class AbstractComparableAggregation<T extends Comparable<T>> implements SimpleAggregation<T> {
	protected AggregateFunction aggregateFunction;

	private ReferenceCounter<T> referenceCounter;

	protected AbstractComparableAggregation(AggregateFunction function) {
		aggregateFunction = function;

		if (function == AggregateFunction.Min || function == AggregateFunction.Max) {
			referenceCounter = new ReferenceCounter<T>();
		}
		else {
			referenceCounter = null;
		}
	}

	public void addChildValue(T value) {
		referenceCounter.addReference(value);
	}

	public void removeChildValue(T value) {
		referenceCounter.removeReference(value);
	}

	public T getValue() {

		// T ret = default(T);
		T ret = null;
		boolean isfirst = true;

		for (T key : referenceCounter.getReferenceCounts().keySet()) {

			int count = referenceCounter.getReferenceCount(key);

			if (count <= 0)
				continue;

			if (isfirst) {
				ret = key;
				isfirst = false;
				continue;
			}

			if (aggregateFunction == AggregateFunction.Min && key.compareTo(ret) < 0
					|| /* aggregateFunction == AggregateFunction.Max && */ key.compareTo(ret) > 0) {

				ret = key;
			}
		}

		return ret;
	}
}

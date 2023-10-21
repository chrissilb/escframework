package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.states.aggregations.Aggregation;
import de.gwasch.code.escframework.states.aggregations.MaxAggregation;
import de.gwasch.code.escframework.states.aggregations.MinAggregation;
import de.gwasch.code.escframework.states.utils.Mask;
import de.gwasch.code.escframework.states.utils.MaskPool;
import de.gwasch.code.escframework.states.utils.MaskPair;

public abstract class AbstractISState<T extends Enum<T>> extends CompositeState<T> {

	private Mask<T, T> startMask;
	private Mask<T, T> stopMask;

	protected StrivingState<T> rootState;

	protected AbstractISState(Class<T> stateType, String name, State<T> strivedState) {
		super(stateType, name);

		State<T> ss = new StrivingState<T>(stateType, name, RestrictionType.OFFENSIVE, strivedState);
		rootState = new StrivingState<T>(stateType, name, ss);

		MaskPair<T> maskset = MaskPool.getInstance().getMaskPair(stateType);

		if (maskset != null) {
			startMask = maskset.getStartMask();
			stopMask = maskset.getStopMask();
		} else {
			if (!MaskPool.getInstance().hasMaskPair(stateType)) {
				MaskPool.getInstance().addMaskPair(stateType);
			}

			startMask = null;
			stopMask = null;
		}
	}

	protected State<T> getRootState() {
		return rootState;
	}

	// todo, BrakedSate ist eigentlich nur bei sequentiellem Transitionmodus
	// sinnvoll
	public void addDependency(ISState<T> dependency, DependencyType startType, DependencyType stopType) {
		addStartDependency(rootState, dependency, startMask);
		addStopDependency(dependency.getRootState(), this, stopMask);

		if (startType == DependencyType.Active) {
			State<T> brakestate = new BrakedState<T>(getStateType(), this, rootState.getStrivedState());
			addStartDependency(dependency.rootState.getStrivedState(), brakestate, stopMask);
		}

		if (stopType == DependencyType.Active) {
			State<T> brakestate = new BrakedState<T>(getStateType(), dependency,
					dependency.rootState.getStrivedState());
			addStopDependency(rootState.getStrivedState(), brakestate, startMask);
		}
	}

	public void addDependency(ISState<T> dependency, DependencyType type) {
		addDependency(dependency, type, type);
	}

	public void addDependency(ISState<T> dependency) {
		addDependency(dependency, DependencyType.Passive, DependencyType.Passive);
	}

	private void addStartDependency(State<T> strivingState, State<T> startDependency, Mask<T, T> mask) {
		StrivingState<T> striving = (StrivingState<T>) strivingState;

		if (striving.getStartRestriction() == null) {
			if (mask != null)
				startDependency = new MaskedState<T, T>(getStateType(), "start mask", startDependency, mask);
			striving.setStartRestriction(startDependency);
		} else {
			MaskedState<T, T> maskedState = null;
			AggregateState<T> aggregated = null;
			State<T> singlechild = null;

			if (mask != null) {
				maskedState = (MaskedState<T, T>) striving.getStartRestriction();

				if (maskedState.getParamState() instanceof AggregateState) {
					aggregated = (AggregateState<T>) maskedState.getParamState();
				} else {
					singlechild = maskedState.getParamState();
				}
			} else {
				if (striving.getStartRestriction() instanceof AggregateState) {
					aggregated = (AggregateState<T>) striving.getStartRestriction();
				} else {
					singlechild = striving.getStartRestriction();
				}
			}

			if (aggregated == null) {

				Aggregation<T> aggregation;

				switch (striving.getRestrictionType()) {
				case DEFENSIVE:
					aggregation = new MinAggregation<T>();
					break;
				case OFFENSIVE:
					aggregation = new MaxAggregation<T>();
					break;
				default:
					return;
				}

				aggregated = new AggregateState<T>(getStateType(), "startdep " + strivingState.getName(), aggregation);

				if (maskedState != null) {
					maskedState.setParamState(aggregated);
					striving.setStartRestriction(maskedState);
				} else
					striving.setStartRestriction(aggregated);

				aggregated.addChildState(singlechild);
			}

			aggregated.addChildState(startDependency);
		}
	}

	private void addStopDependency(State<T> strivingState, State<T> stopDependency, Mask<T, T> mask) {
		StrivingState<T> striving = (StrivingState<T>) strivingState;

		if (striving.getStopRestriction() == null) {
			if (mask != null)
				stopDependency = new MaskedState<T, T>(getStateType(), "stop mask", stopDependency, mask);
			striving.setStopRestriction(stopDependency);
		} else {
			MaskedState<T, T> maskedState = null;
			AggregateState<T> aggregated = null;
			State<T> singlechild = null;

			if (mask != null) {
				maskedState = (MaskedState<T, T>) striving.getStopRestriction();

				if (maskedState.getParamState() instanceof AggregateState) {
					aggregated = (AggregateState<T>) maskedState.getParamState();
				} else {
					singlechild = maskedState.getParamState();
				}

			} else {
				if (striving.getStopRestriction() instanceof AggregateState) {
					aggregated = (AggregateState<T>) striving.getStopRestriction();
				} else {
					singlechild = striving.getStopRestriction();
				}
			}

			if (aggregated == null) {

				Aggregation<T> aggregation;

				switch (striving.getRestrictionType()) {
				case DEFENSIVE:
					aggregation = new MaxAggregation<T>();
					break;
				case OFFENSIVE:
					aggregation = new MinAggregation<T>();
					break;
				default:
					return;
				}

				aggregated = new AggregateState<T>(getStateType(), "stopdep " + strivingState.getName(), aggregation);

				if (maskedState != null) {
					maskedState.setParamState(aggregated);
					striving.setStopRestriction(maskedState);
				} else {
					striving.setStopRestriction(aggregated);
				}

				aggregated.addChildState(singlechild);
			}

			aggregated.addChildState(stopDependency);
		}
	}
}
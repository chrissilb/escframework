package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.utils.EnumUtil;

/**
 * A {@code StrivingState} has a strived state which can be restricted by a
 * start or a stop restrictive state in order to become the exposed state.
 * {@code StrivingState} values can be enum values, only. A start restrictive
 * state can prevent to reach a higher strived state; "higher" in terms of an
 * enum value with a greater ordinal number. On the other hand, a stop
 * restrictive state can prevent to reach a lower strived state; "lower" in
 * therms of an enum value with a lower ordinal number.
 * <p>
 * The actual influence of start and stop restrictive states is defined by the
 * restriction type which can be offensive or defensive. The following table
 * shows how the exposed state value is calculated:
 * <table border="1">
 * <caption>Calculation of exposed values</caption>
 * <tr>
 * <th>Restriction Type</th>
 * <th>Start Transition</th>
 * <th>Stop Transition</th>
 * </tr>
 * <tr>
 * <th>DEFENSIVE</th>
 * <td>min(strived, start restictive)</td>
 * <td>max(strived, stop restrictive)</td>
 * </tr>
 * <tr>
 * <th>OFFENSIVE</th>
 * <td>max(strived, start restrictive)</td>
 * <td>min(strived, stop restictive)</td>
 * </tr>
 * </table>
 * If the strived state matches the exposed state it is called a consonant
 * state, otherwise a dissonant state.
 * 
 * @param <T> the type of the {@code State} value
 */
public class StrivingState<T extends Enum<T>> extends State<T> {

	class OnStrivedTransition extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {

			if (gt(strivedState, StrivingState.this)) {
				doTransition(startRestrictiveState);
			} else if (lt(strivedState, StrivingState.this)) {
				doTransition(stopRestrictiveState);
			}

			return true;
		}
	}

	class OnStartRestrictionTransition extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {

			if (EnumUtil.gt(event.getNewValue(), event.getOldValue())) {
				if (gt(startRestrictiveState, StrivingState.this))
					doTransition(startRestrictiveState);
			} else if (EnumUtil.lt(event.getNewValue(), event.getOldValue())
					&& restrictionType == RestrictionType.OFFENSIVE) {
				if (lt(startRestrictiveState, StrivingState.this))
					doTransition(startRestrictiveState);
			}

			return true;
		}
	}

	class OnStopRestrictionTransition extends EventAdapter<TransitionEvent<T>> {

		public boolean onProcess(TransitionEvent<T> event) {

			if (EnumUtil.lt(event.getNewValue(), event.getOldValue())) {
				if (lt(stopRestrictiveState, StrivingState.this))
					doTransition(stopRestrictiveState);
			} else if (EnumUtil.gt(event.getNewValue(), event.getOldValue())
					&& restrictionType == RestrictionType.OFFENSIVE) {
				if (gt(stopRestrictiveState, StrivingState.this))
					doTransition(stopRestrictiveState);
			}

			return true;
		}
	}

	private RestrictionType restrictionType;

	private State<T> strivedState;
	private State<T> startRestrictiveState;
	private State<T> stopRestrictiveState;

	public StrivingState(Class<T> stateType, String name, RestrictionType restrictionType, State<T> strivedState) {
		super(stateType, name);

		if (restrictionType == RestrictionType.DEFENSIVE) {
			setName("Def:" + getName());
		} else {
			setName("Off:" + getName());
		}

		this.restrictionType = restrictionType;

		this.strivedState = strivedState;
		strivedState.registerTransitionListener(new OnStrivedTransition());
	}

	public StrivingState(Class<T> stateType, String name, RestrictionType restrictionType) {
		this(stateType, name, restrictionType, new SimpleState<T>(stateType, name));
	}

	public StrivingState(Class<T> stateType, String name, State<T> strivedState) {
		this(stateType, name, RestrictionType.DEFENSIVE, strivedState);
	}

	public RestrictionType getRestrictionType() {
		return restrictionType;
	}

	public State<T> getStartRestriction() {
		return startRestrictiveState;
	}

	public void setStartRestriction(State<T> restriction) {

		if (startRestrictiveState == null) {
			restriction.registerTransitionListener(new OnStartRestrictionTransition());
		}

		startRestrictiveState = restriction;
	}

	public State<T> getStopRestriction() {
		return stopRestrictiveState;
	}

	public void setStopRestriction(State<T> restriction) {

		if (stopRestrictiveState == null) {
			restriction.registerTransitionListener(new OnStopRestrictionTransition());
		}

		stopRestrictiveState = restriction;
	}

	public void setValue(T value) {

		if (strivedState.getValue().equals(value) && !getValue().equals(value)
				&& (restrictionType == RestrictionType.OFFENSIVE || getActivityHandler() != null)) {

			strivedState.fireTransitionEvents();
		} else {
			strivedState.setValue(value);
		}
	}

	public State<T> getStrivedState() {
		return strivedState;
	}

	private void doTransition(State<T> depState) {

		if (depState != null) {
			if ((restrictionType == RestrictionType.DEFENSIVE && depState == startRestrictiveState)
					|| (restrictionType == RestrictionType.OFFENSIVE && depState == stopRestrictiveState)) {

				setStateValue(min(depState, strivedState));
			} else {
				setStateValue(max(depState, strivedState));
			}
		} else {
			setStateValue(strivedState.getValue());
		}
	}

	private static <T extends Enum<T>> T min(State<T> state1, State<T> state2) {
		State<T> state = (lt(state1, state2)) ? state1 : state2;
		return state.getValue();
	}

	private static <T extends Enum<T>> T max(State<T> state1, State<T> state2) {
		State<T> state = (gt(state1, state2)) ? state1 : state2;
		return state.getValue();
	}

	private static <T extends Enum<T>> boolean gt(State<T> state1, State<T> state2) {
		return EnumUtil.gt(state1.getValue(), state2.getValue());
	}

	private static <T extends Enum<T>> boolean lt(State<T> state1, State<T> state2) {
		return EnumUtil.lt(state1.getValue(), state2.getValue());
	}
}
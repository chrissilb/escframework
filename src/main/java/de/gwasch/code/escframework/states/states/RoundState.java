package de.gwasch.code.escframework.states.states;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

/**
 * {@code RoundState} rounds a {@code Double} state "half up". Both the number itself
 * and the amount of decimal places is defined by {@code State} variables.
 */
public class RoundState extends State<Double> {

	class NumberTransitionHandler extends EventAdapter<TransitionEvent<Double>> {

		public boolean onProcess(TransitionEvent<Double> event) {
			updateValue(event.getNewValue(), digitsState.getValue());
			return true;
		}
	}

	class DigitsTransitionHandler extends EventAdapter<TransitionEvent<Integer>> {

		public boolean onProcess(TransitionEvent<Integer> event) {
			updateValue(numberState.getValue(), event.getNewValue());
			return true;
		}
	}

	private State<Double> numberState;
	private State<Integer> digitsState;

	private NumberTransitionHandler numberTransitionHandler;
	private DigitsTransitionHandler digitsTransitionHandler;

	/**
	 * Constructs a {@code RoundState}.
	 * 
	 * @param name the state name
	 * @param numberState Its value is used as the exposed state value to be rounded.
	 * @param digitsState Its value is used as the number of decimal places to be considered.
	 */
	public RoundState(String name, State<Double> numberState, State<Integer> digitsState) {
		super(Double.class, name);
		this.numberState = numberState;
		this.digitsState = digitsState;

		updateValue(numberState.getValue(), digitsState.getValue());
		numberTransitionHandler = new NumberTransitionHandler();
		digitsTransitionHandler = new DigitsTransitionHandler();
		numberState.registerTransitionListener(numberTransitionHandler);
		digitsState.registerTransitionListener(digitsTransitionHandler);
	}

	/**
	 * Returns the number state.
	 * @return the number state
	 */
	public State<Double> getNumberState() {
		return numberState;
	}

	/**
	 * Sets the number state
	 * @param numberState the number state
	 */
	public void setNumberState(State<Double> numberState) {

		this.numberState.unregisterTransitionListener(numberTransitionHandler);

		this.numberState = numberState;
		updateValue(numberState.getValue(), digitsState.getValue());

		numberState.registerTransitionListener(numberTransitionHandler);
	}

	/**
	 * Returns the state which value indicates the number of decimal places.
	 * @return the state which value indicates the number of decimal places
	 */
	public State<Integer> getDigitsState() {
		return digitsState;
	}

	/**
	 * Sets the state which value indicates the number of decimal places.
	 * @param digitsState The state which value indicates the number of decimal places.
	 */
	public void setDigitsState(State<Integer> digitsState) {

		this.digitsState.unregisterTransitionListener(digitsTransitionHandler);

		this.digitsState = digitsState;
		updateValue(numberState.getValue(), digitsState.getValue());

		digitsState.registerTransitionListener(digitsTransitionHandler);
	}

	private void updateValue(double number, int digits) {
		BigDecimal bd = new BigDecimal(number);
		bd.setScale(digits, RoundingMode.HALF_UP);
		setStateValue(bd.doubleValue());
	}
}

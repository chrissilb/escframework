package de.gwasch.code.escframework.states.states;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;

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
    
	public State<Double> getNumberState() {
		return numberState;
	}

	public void setNumberState(State<Double> numberState) {

		this.numberState.unregisterTransitionListener(numberTransitionHandler);

		this.numberState = numberState;
		updateValue(numberState.getValue(), digitsState.getValue());

		numberState.registerTransitionListener(numberTransitionHandler);
	}

	public State<Integer> getDigitsState() {
		return digitsState;
	}

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






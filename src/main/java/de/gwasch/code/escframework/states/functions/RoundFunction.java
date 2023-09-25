package de.gwasch.code.escframework.states.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.gwasch.code.escframework.events.handler.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.states.State;
import de.gwasch.code.escframework.states.states.SimpleState;

public class RoundFunction {
	
	class NumberTransitionHandler extends EventAdapter<TransitionEvent<Double>> {
		
		public boolean onProcess(TransitionEvent<Double> event) {
			result.setValue(getValue());
			return true;
		}
	}	
	
	class DigitsTransitionHandler extends EventAdapter<TransitionEvent<Integer>> {
		
		public boolean onProcess(TransitionEvent<Integer> event) {
			result.setValue(getValue());
			return true;
		}
	}		
	
    private SimpleState<Double> result;
    private State<Double> number;
    private State<Integer> digits;
    
    private NumberTransitionHandler numberTransitionHandler;
    private DigitsTransitionHandler digitsTransitionHandler;


    public RoundFunction(SimpleState<Double> result, State<Double> number, State<Integer> digits) {
    	this.result = result;
        this.number = number;
        this.digits = digits;
        numberTransitionHandler = new NumberTransitionHandler();
        digitsTransitionHandler = new DigitsTransitionHandler();
        
        number.registerTransitionListener(numberTransitionHandler);
        digits.registerTransitionListener(digitsTransitionHandler);
    }

    private Double getValue() {
    	BigDecimal bd = new BigDecimal(number.getValue());
    	bd.setScale(digits.getValue(), RoundingMode.HALF_UP);
    	return bd.doubleValue();
    }
}






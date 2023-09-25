package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.handler.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.functions.Function;

public class FunctionState<T> extends State<T> {

	class ParamsTransitionHandler extends EventAdapter<TransitionEvent<?>> {
		
		public boolean onProcess(TransitionEvent<?> event) {
			
			if (function != null) {
				setStateValue(function.getValue());
			}
			
	        return true;
		}
	}	
	
	private Function<T> function;
	private ParamsTransitionHandler paramsTransitionHandler;
	
	public FunctionState(Class<T> stateType, String name) {
		super(stateType, name);

		function = null;
		paramsTransitionHandler = new ParamsTransitionHandler();
	}

    public void addParamState(State<?> state) {
        state.registerAnyTransitionListener(paramsTransitionHandler);
        paramsTransitionHandler.onProcess(null);
    }
    
    public void setFunction(Function<T> function) {
    	this.function = function;
        paramsTransitionHandler.onProcess(null);
    }
}

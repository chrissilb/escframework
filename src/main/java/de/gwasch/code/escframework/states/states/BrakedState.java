package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.handler.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.utils.EnumUtil;

public class BrakedState<T extends Enum<T>> extends State<T> {

	class TransitionHandler extends EventAdapter<TransitionEvent<T>> {
		
		//todo, Daten über Referenztypen könnten schon wieder aktueller sein als das Event...
		public boolean onProcess(TransitionEvent<T> event) {
			
			int r = strivedState.getValue().compareTo(realState.getValue());

	        if (r > 0) {
	            T state = realState.getValue();
	            state = EnumUtil.increment(state);
	            setStateValue(state);
	        }
	        else if (r < 0) {
	            T state = realState.getValue();
	            state = EnumUtil.decrement(state);
	            setStateValue(state);
	        }
	        else {
	            setStateValue(strivedState.getValue());
	        }	
	        
	        return true;
		}
	}
	
    private State<T> realState;
    private State<T> strivedState;

    public BrakedState(Class<T> stateType, State<T> realstate, State<T> strivedstate) {
        super(stateType, "braked " + strivedstate.getName());
    
        realState = realstate;
        strivedState = strivedstate;

        TransitionHandler th = new TransitionHandler();
        realState.registerTransitionListener(th);
        strivedState.registerTransitionListener(th);

        th.onProcess(new TransitionEvent<T>(realState, realState.getValue(), realState.getValue()));
    }


}
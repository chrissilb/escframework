package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.utils.EnumUtil;

public class BrakedState<T extends Enum<T>> extends State<T> {

	class TransitionHandler extends EventAdapter<TransitionEvent<T>> {
		
		//todo, Daten über Referenztypen könnten schon wieder aktueller sein als das Event...
		public boolean onProcess(TransitionEvent<T> event) {
			
			int r = strivedState.getValue().compareTo(actualState.getValue());

	        if (r > 0) {
	            T state = actualState.getValue();
	            state = EnumUtil.increment(state);
	            setStateValue(state);
	        }
	        else if (r < 0) {
	            T state = actualState.getValue();
	            state = EnumUtil.decrement(state);
	            setStateValue(state);
	        }
	        else {
	            setStateValue(strivedState.getValue());
	        }	
	        
	        return true;
		}
	}
	
    private State<T> actualState;
    private State<T> strivedState;

    public BrakedState(Class<T> stateType, State<T> actualState, State<T> strivedState) {
        super(stateType, "braked " + strivedState.getName());
    
        this.actualState = actualState;
        this.strivedState = strivedState;

        TransitionHandler th = new TransitionHandler();
        actualState.registerTransitionListener(th);
        strivedState.registerTransitionListener(th);

        th.onProcess(new TransitionEvent<T>(actualState, actualState.getValue(), actualState.getValue()));
    }


}
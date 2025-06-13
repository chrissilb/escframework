package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.listeners.EventListener;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.listeners.ActivityHandler;
import de.gwasch.code.escframework.states.transistionmodes.TransitionMode;

public abstract class CompositeState<T> extends State<T> {
	
    public CompositeState(Class<T> stateType, String name) {
        super(stateType, name);
    }

    protected abstract State<T> getRootState();


    public ActivityHandler<T> getActivityListener() {
    	return getRootState().getActivityListener();
    }
    
    public void setActivityHandler(ActivityHandler<T> handler) {
    	getRootState().setActivityHandler(handler);
    }
   
    public TransitionMode<T> getTransitionMode() {
    	return getRootState().getTransitionMode();
    }
    
    public void setTransitionMode(TransitionMode<T> mode) {
    	getRootState().setTransitionMode(mode);
    }
    

    public String getName() {
    	return getRootState().getName();
    }
   

    public T getValue() {
    	return getRootState().getValue();
    }
    
    public void setValue(T value) {
    	getRootState().setValue(value);
    }
  
    public void registerTransitionListener(EventListener<TransitionEvent<T>> listener) {
    	getRootState().registerTransitionListener(listener);
    }   
        
    public void unregisterTransitionListener(EventListener<TransitionEvent<T>> listener) {
    	getRootState().unregisterTransitionListener(listener);
    }

    public void fireTransitionEvents() { 
    	getRootState().fireTransitionEvents(); 
    }
}
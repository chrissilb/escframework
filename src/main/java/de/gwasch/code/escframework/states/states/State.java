package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.listeners.EventListener;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Processor;
import de.gwasch.code.escframework.events.processors.Scheduler;
import de.gwasch.code.escframework.events.utils.PNBuilder;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.listeners.ActivityListener;
import de.gwasch.code.escframework.states.transistionmodes.DirectTransitionMode;
import de.gwasch.code.escframework.states.transistionmodes.TransitionMode;

/**
 * {@code State} is the main class of all states. All states communicate via
 * their own processor network.
 * 
 * @param <T> the type of the {@code State} value
 */
public abstract class State<T> {

	private static Processor<Event> eventProcessor;
	private static Dispatcher<Event> eventDispatcher;

	static {
		eventProcessor = new PNBuilder<Event>("transition").add(new Scheduler<>())
				.add(eventDispatcher = new Dispatcher<>()).top();

		eventProcessor.activate();
	}

	private String name;

	private TransitionMode<T> transitionMode;
	private ActivityListener<T> activityHandler;

	private Class<T> stateType;
	private T stateValue;
	private Class<TransitionEvent<T>> transitionEventClass;

	private TransitionEvent<?> anyTransitionEvent;
	private Class<TransitionEvent<?>> anyTransitionEventClass;

	@SuppressWarnings("unchecked")
	protected State(Class<T> stateType, String name) {

		this.stateType = stateType;

		TransitionEvent<T> transitionEvent = new TransitionEvent<T>(this, null, null);

		transitionEventClass = (Class<TransitionEvent<T>>) transitionEvent.getClass();

		anyTransitionEvent = new TransitionEvent<>(this, null, null);
		anyTransitionEventClass = (Class<TransitionEvent<?>>) anyTransitionEvent.getClass();

		this.name = name;

		transitionMode = new DirectTransitionMode<T>();

		if (stateType.isEnum()) {
			stateValue = stateType.getEnumConstants()[0];
		} else if (stateType.equals(Boolean.class)) {
			stateValue = (T) Boolean.FALSE;
		} else if (stateType.equals(Integer.class)) {
			stateValue = (T) Integer.valueOf(0);
		} else if (stateType.equals(Double.class)) {
			stateValue = (T) Double.valueOf(0.0);
		} else if (stateType.equals(Byte.class)) {
			stateValue = (T) Byte.valueOf((byte) 0);
		} else if (stateType.equals(Short.class)) {
			stateValue = (T) Short.valueOf((short) 0);
		} else if (stateType.equals(Long.class)) {
			stateValue = (T) Long.valueOf(0L);
		} else if (stateType.equals(Float.class)) {
			stateValue = (T) Float.valueOf(0.0f);
		} else if (stateType.equals(Character.class)) {
			stateValue = (T) Character.valueOf('\0');
		} else {
			stateValue = null;
		}

	}

	public void registerTransitionListener(EventListener<TransitionEvent<T>> listener) {
		eventDispatcher.register(this, transitionEventClass, listener);
	}

	public void unregisterTransitionListener(EventListener<TransitionEvent<T>> listener) {
		eventDispatcher.unregister(this, transitionEventClass, listener);
	}

	public void registerAnyTransitionListener(EventListener<TransitionEvent<?>> listener) {
		eventDispatcher.register(this, anyTransitionEventClass, listener);
	}

	public void unregisterAnyTransitionListener(EventListener<TransitionEvent<?>> listener) {
		eventDispatcher.unregister(this, transitionEventClass, listener);
	}

	public ActivityListener<T> getActivityHandler() {
		return activityHandler;
	}

	public void setActivityHandler(ActivityListener<T> handler) {
		activityHandler = handler;
	}

	public TransitionMode<T> getTransitionMode() {
		return transitionMode;
	}

	public void setTransitionMode(TransitionMode<T> mode) {
		transitionMode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Class<T> getStateType() {
		return stateType;
	}

	protected T getStateValue() {
		return stateValue;
	}

	protected void setStateValue(T value) {

		while (stateValue != value) {

			T newValue = transitionMode.singleTransition(value, stateValue);

			if (stateValue == newValue)
				return;

			if (activityHandler != null && !activityHandler.activity(newValue, stateValue))
				return;

			T oldValue = stateValue;
			stateValue = newValue;

			fireTransitionEvents(oldValue);
		}
	}

	public T getValue() {
		return stateValue;
	}

	public void setValue(T value) {
		throw new UnsupportedOperationException();
	}

	public void fireTransitionEvents() {
		fireTransitionEvents(getValue());
	}

	private void fireTransitionEvents(T oldValue) {

		TransitionEvent<T> event = new TransitionEvent<T>(this, getStateValue(), oldValue);
		eventProcessor.process(event);
	}

//    public boolean equals(Object obj) {
//    	AbstractState<T> cmp = (AbstractState<T>)obj;
//    	boolean ret = getValue().equals(cmp.getValue());
//    	return ret;
//    }
//    
//    public int hashCode() {
//    	return getValue().hashCode();
//    }

	public String toString() {
		return name + ": " + stateValue;
	}
}

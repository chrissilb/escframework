package de.gwasch.code.escframework.states.states;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.listeners.EventListener;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Processor;
import de.gwasch.code.escframework.events.processors.Scheduler;
import de.gwasch.code.escframework.events.utils.PNBuilder;
import de.gwasch.code.escframework.states.events.TransitionEvent;
import de.gwasch.code.escframework.states.listeners.ActivityHandler;
import de.gwasch.code.escframework.states.transistionmodes.DirectTransitionMode;
import de.gwasch.code.escframework.states.transistionmodes.TransitionMode;
import de.gwasch.code.escframework.states.utils.TypeUtil;

/**
 * {@code State} is the main class of all states. All states communicate via
 * their common state processor network. A State allows a transition listener to
 * get informed about state value changes. Such a listener might be dependent on
 * a specific value type or not. Furthermore, an activity handler can be
 * installed to influence state transitions.
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
	private ActivityHandler<T> activityHandler;

	private Class<T> stateType;
	private T stateValue;
	private Class<TransitionEvent<T>> transitionEventClass;

	private TransitionEvent<?> anyTransitionEvent;
	private Class<TransitionEvent<?>> anyTransitionEventClass;

	/**
	 * Constructs a {@code State}.
	 * 
	 * @param stateType the type of state values
	 * @param name      the state name
	 */
	@SuppressWarnings("unchecked")
	protected State(Class<T> stateType, String name) {

		this.stateType = stateType;

		TransitionEvent<T> transitionEvent = new TransitionEvent<T>(this, null, null);

		transitionEventClass = (Class<TransitionEvent<T>>) transitionEvent.getClass();

		anyTransitionEvent = new TransitionEvent<>(this, null, null);
		anyTransitionEventClass = (Class<TransitionEvent<?>>) anyTransitionEvent.getClass();

		this.name = name;

		transitionMode = new DirectTransitionMode<T>();

		stateValue = TypeUtil.getDefaultValue(stateType);
	}

	/**
	 * Registers for state transitions of a specific type.
	 * 
	 * @param listener the listener instance
	 */
	public void registerTransitionListener(EventListener<TransitionEvent<T>> listener) {
		eventDispatcher.register(this, transitionEventClass, listener);
	}

	/**
	 * Unregisters for state transitions of a specific type.
	 * 
	 * @param listener the listener instance
	 */
	public void unregisterTransitionListener(EventListener<TransitionEvent<T>> listener) {
		eventDispatcher.unregister(this, transitionEventClass, listener);
	}

	/**
	 * Registers for state transitions of any type.
	 * 
	 * @param listener the listener instance
	 */
	public void registerAnyTransitionListener(EventListener<TransitionEvent<?>> listener) {
		eventDispatcher.register(this, anyTransitionEventClass, listener);
	}

	/**
	 * Unregisters for state transitions of any type.
	 * 
	 * @param listener the listener instance
	 */
	public void unregisterAnyTransitionListener(EventListener<TransitionEvent<?>> listener) {
		eventDispatcher.unregister(this, transitionEventClass, listener);
	}

	/**
	 * Returns the activity handler which can block state transitions.
	 * 
	 * @return the activity handler
	 */
	public ActivityHandler<T> getActivityListener() {
		return activityHandler;
	}

	/**
	 * Sets the activity handler.
	 * 
	 * @param handler the activity handler
	 */
	public void setActivityHandler(ActivityHandler<T> handler) {
		activityHandler = handler;
	}

	/**
	 * Returns the transition mode which defines for a {@code State} how to get from
	 * a current value to a target value.
	 * 
	 * @return the transition mode
	 */
	public TransitionMode<T> getTransitionMode() {
		return transitionMode;
	}

	/**
	 * Sets the transition mode.
	 * 
	 * @param mode the transition mode
	 */
	public void setTransitionMode(TransitionMode<T> mode) {
		transitionMode = mode;
	}

	/**
	 * Returns the state name.
	 * 
	 * @return the state name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the state name
	 * 
	 * @param name the state name
	 */
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

	/**
	 * Returns the exposed value of the state.
	 * @return the exposed value of the state
	 */
	public T getValue() {
		return stateValue;
	}

	/**
	 * Sets the exposed value of the state. This method is not support by most of
	 * the {@code State} classes because its value is calculated. One exception to
	 * this is {@link SimpleState}.
	 * 
	 * @param value the new exposed value
	 * @throws UnsupportedOperationException if explicit setting of values is not
	 *                                       allowed by the {@code State} class.
	 */
	public void setValue(T value) {
		throw new UnsupportedOperationException();
	}

	protected void fireTransitionEvents() {
		fireTransitionEvents(getValue());
	}

	private void fireTransitionEvents(T oldValue) {

		TransitionEvent<T> event = new TransitionEvent<T>(this, getStateValue(), oldValue);
		eventProcessor.process(event);
	}

	/**
	 * Returns a string representation of the object.
	 * @return a string representation of the object
	 */
	public String toString() {
		return name + ": " + stateValue;
	}
}

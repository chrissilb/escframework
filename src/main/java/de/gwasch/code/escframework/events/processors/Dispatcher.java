package de.gwasch.code.escframework.events.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import de.gwasch.code.escframework.events.events.ActivateEvent;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.DeactivateEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.listeners.CallbackListener;
import de.gwasch.code.escframework.events.listeners.EventListener;
import de.gwasch.code.escframework.events.listeners.ProcessListener;
import de.gwasch.code.escframework.events.listeners.StateListener;

//todo, StateListener-einhängen berücksichtigen

/**
 * A {@code Dispatcher} informs {@link EventListener} about processed
 * {@link Event}s. It allows filtering by an event source (defined by
 * {@link Event#getSource()} and by the definition of a {@link Predicate}.
 * Furthermore, {@link StateListener}s can be registered to observe the status
 * of the processor network.
 * 
 * <p>
 * {@code Dispatcher}s integrate themselves into the processor network once the
 * first {@code EventListener} is registered. So they produce no overhead if
 * they are not used. Although, they do not "hang out" themselves once the last
 * {@code EventListener} unregisters to avoid overhead by this. This is
 * equivalent for the {@code StateListener}. Once needed a {@code Dispatcher}
 * makes sure to be considered for all {@link ProcessListener}s.
 * 
 * @param <E> the event type considered by the {@code Dispatcher}
 * 
 * @see EventListener
 */
public class Dispatcher<E extends Event> extends Processor<E> {

	private List<EventListener<E>> listeners = null;

	// todo, Laufzeitoptimierung: Listeners im Vorfeld aufbereiten

	private void retrieveListener(Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap,
			Class<?> eventClass) {

		if (innerMap.containsKey(eventClass)) {
			for (EventListener<? extends Event> listener : innerMap.get(eventClass)) {
				@SuppressWarnings("unchecked")
				EventListener<E> l = (EventListener<E>) listener;
				listeners.add(l);
			}
		}

		if (eventClass.getSuperclass() != null) {
			retrieveListener(innerMap, eventClass.getSuperclass());
		}

		if (eventClass.equals(Event.class)) {
			return;
		}

		for (Class<?> interfaceClass : eventClass.getInterfaces()) {
			if (Event.class.isAssignableFrom(interfaceClass)) {
				retrieveListener(innerMap, interfaceClass);
			}
		}
	}

	private List<EventListener<E>> retrieveListener(E event) {

		listeners = new LinkedList<>();
		Object source = event.getSource();

		if (eventListenerMap.containsKey(null)) {
			Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap = eventListenerMap.get(null);
			retrieveListener(innerMap, event.getClass());
		}

		if (source != null) {
			if (eventListenerMap.containsKey(source)) {
				Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap = eventListenerMap
						.get(source);
				retrieveListener(innerMap, event.getClass());
			}
		}

		return listeners;
	}

	class ProcessHandler implements ProcessListener<E> {

		public void process(E event) {

			if (nrActiveEvents == 0) {
				for (StateListener listener : stateListeners) {
					listener.onWorking();
				}
			}

			nrActiveEvents++;

			retrieveListener(event);
			boolean forward = true;

			for (EventListener<E> listener : listeners) {
				if (predicateMap.containsKey(listener)) {
					@SuppressWarnings("unchecked")
					Predicate<E> predicate = (Predicate<E>) predicateMap.get(listener);
					if (!predicate.test(event)) {
						continue;
					}
				}
				forward &= listener.onProcess(event);
			}

			if (forward) {
				forward(event, callbackHandler);
			} else {
				callback(event, false);
			}
		}
	}

	class CallbackHandler implements CallbackListener<E> {

		public void finish(E event, boolean success) {

			nrActiveEvents--;

			callback(event, success);

			retrieveListener(event);

			for (EventListener<E> listener : listeners) {
				listener.onFinish(event, success);
			}

			// NOTE: callback before this check to consider this does not lead to further
			// events, esp. with Inlets
			if (nrActiveEvents == 0) {
				for (StateListener listener : stateListeners) {
					listener.onIdle();
				}
			}
		}
	}

	class ActivateHandler implements ProcessListener<ActivateEvent> {

		public void process(ActivateEvent event) {

			for (StateListener listener : stateListeners) {
				listener.onActivate();
			}

			forwardActivate(event, activateCallbackHandler);
		}
	}

	class ActivateCallbackHandler implements CallbackListener<ActivateEvent> {

		public void finish(ActivateEvent event, boolean success) {

			for (StateListener listener : stateListeners) {
				listener.onActivated(success);
			}

			callback(event, success);
		}
	}

	class DeactivateHandler implements ProcessListener<DeactivateEvent> {

		public void process(DeactivateEvent event) {

			for (StateListener listener : stateListeners) {
				listener.onDeactivate();
			}

			forwardDeactivate(event, deactivateCallbackHandler);
		}
	}

	class DeactivateCallbackHandler implements CallbackListener<DeactivateEvent> {

		public void finish(DeactivateEvent event, boolean success) {

			for (StateListener listener : stateListeners) {
				listener.onDeactivated(success);
			}

			callback(event, success);
		}
	}

	class SuspendHandler implements ProcessListener<SuspendEvent> {

		public void process(SuspendEvent event) {

			for (StateListener listener : stateListeners) {
				listener.onSuspend();
			}

			forwardSuspend(event, suspendCallbackHandler);
		}
	}

	class SuspendCallbackHandler implements CallbackListener<SuspendEvent> {

		public void finish(SuspendEvent event, boolean success) {

			for (StateListener listener : stateListeners) {
				listener.onSuspended(success);
			}

			callback(event, success);
		}
	}

	class ResumeHandler implements ProcessListener<ResumeEvent> {

		public void process(ResumeEvent event) {

			for (StateListener listener : stateListeners) {
				listener.onResume();
			}

			forwardResume(event, resumeCallbackHandler);
		}
	}

	class ResumeCallbackHandler implements CallbackListener<ResumeEvent> {

		public void finish(ResumeEvent event, boolean success) {

			for (StateListener listener : stateListeners) {
				listener.onResumed(success);
			}

			callback(event, success);
		}
	}

	class CancelHandler implements ProcessListener<CancelEvent> {

		public void process(CancelEvent event) {

			for (StateListener listener : stateListeners) {
				listener.onCancel();
			}

			forwardCancel(event, cancelCallbackHandler);
		}
	}

	class CancelCallbackHandler implements CallbackListener<CancelEvent> {

		public void finish(CancelEvent event, boolean success) {

			for (StateListener listener : stateListeners) {
				listener.onCancelled(success);
			}

			callback(event, success);
		}
	}

	private Map<Object, Map<Class<? extends Event>, List<EventListener<? extends Event>>>> eventListenerMap;
	private Map<EventListener<? extends Event>, Predicate<? extends Event>> predicateMap;
	private Set<StateListener> stateListeners;

	private CallbackListener<E> callbackHandler;
	private CallbackListener<ActivateEvent> activateCallbackHandler;
	private CallbackListener<DeactivateEvent> deactivateCallbackHandler;
	private CallbackListener<SuspendEvent> suspendCallbackHandler;
	private CallbackListener<ResumeEvent> resumeCallbackHandler;
	private CallbackListener<CancelEvent> cancelCallbackHandler;

	private int nrActiveEvents;

	/**
	 * Constructs a {@code Dispatcher}.
	 * 
	 * @param name the name of this {@code Processor}
	 */
	public Dispatcher(String name) {
		super(name);

		eventListenerMap = new HashMap<>();
		predicateMap = new HashMap<>();
		stateListeners = new HashSet<>();
		callbackHandler = new CallbackHandler();
		activateCallbackHandler = new ActivateCallbackHandler();
		deactivateCallbackHandler = new DeactivateCallbackHandler();
		suspendCallbackHandler = new SuspendCallbackHandler();
		resumeCallbackHandler = new ResumeCallbackHandler();
		cancelCallbackHandler = new CancelCallbackHandler();
		nrActiveEvents = 0;

//		setHandler(null, new ProcessHandler());
//		installListener(ActivateEvent.class, new ActivateHandler());
//		installListener(DeactivateEvent.class, new DeactivateHandler());
//		installListener(SuspendEvent.class, new SuspendHandler());
//		installListener(ResumeEvent.class, new ResumeHandler());
//		installListener(CancelEvent.class, new CancelHandler());
	}

	/**
	 * Constructs a {@code Dispatcher}. The name of this {@code Processor} is an
	 * empty {@code String}.
	 */
	public Dispatcher() {
		this("");
	}

	/**
	 * Registers an event listener.
	 * 
	 * @param <F>        the type of the registered {@code Event}s
	 * @param source     the event source provided by {@link Event#getSource()}
	 * @param eventClass {@link Class} instance of the event type
	 * @param listener   the event listener
	 */
	public <F extends Event> void register(Object source, Class<F> eventClass, EventListener<? super F> listener) {

		if (!eventListenerMap.containsKey(source)) {
			eventListenerMap.put(source, new HashMap<>());
		}

		Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap = eventListenerMap.get(source);

		if (!innerMap.containsKey(eventClass)) {
			innerMap.put(eventClass, new ArrayList<>());
		}

		innerMap.get(eventClass).add(listener);

		if (getListener(null) == null) {
			installListener(null, new ProcessHandler());
		}
	}

	/**
	 * Registers an event listener.
	 * 
	 * @param <F>        the type of the registered {@code Event}s
	 * @param eventClass {@link Class} instance of the event type
	 * @param listener   the event listener
	 */
	public <F extends Event> void register(Class<F> eventClass, EventListener<? super F> listener) {
		register(null, eventClass, listener);
	}

	/**
	 * Unregisters all event listeners.
	 */
	public void unregisterEventListeners() {
		eventListenerMap.clear();
	}

	/**
	 * Unregisters a specific event listener.
	 * 
	 * @param <F>        the type of the registered {@code Event}s
	 * @param source     the event source provided by {@link Event#getSource()}
	 * @param eventClass {@link Class} instance of the event type
	 * @param listener   the event listener
	 */
	public <F extends Event> void unregister(Object source, Class<F> eventClass, EventListener<? super F> listener) {

		Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap = eventListenerMap.get(source);
		if (innerMap == null) {
			return;
		}

		if (listener == null) {
			innerMap.remove(eventClass);
			return;
		}

		List<EventListener<? extends Event>> listeners = innerMap.get(eventClass);
		if (listeners == null) {
			return;
		}

		if (listeners.size() == 1) {
			if (listeners.contains(listener)) {
				eventListenerMap.remove(eventClass);
			}
		} else {
			listeners.remove(listener);
		}

	}

	/**
	 * Unregisters all event listeners of a specific event type.
	 * 
	 * @param eventClass {@link Class} instance of the event type
	 */
	public void unregister(Class<E> eventClass) {
		unregister(null, eventClass, null);
	}

	/**
	 * Unregisters an event listeners of a specific event type.
	 * 
	 * @param eventClass {@link Class} instance of the event type
	 * @param listener   the event listener
	 */
	public void unregister(Class<E> eventClass, EventListener<? super E> listener) {
		unregister(null, eventClass, listener);
	}

	/**
	 * Unregisters an event listener.
	 * 
	 * @param listener the event listener
	 */
	public void unregister(Object listener) {
		for (Map<Class<? extends Event>, List<EventListener<? extends Event>>> innerMap : eventListenerMap.values()) {
			for (List<EventListener<? extends Event>> listeners : innerMap.values()) {
				listeners.remove(listener);
			}
		}
	}

	/**
	 * Registers a {@link Predicate} as an additional filter for a listener.
	 * 
	 * @param <F>       the type of the registered {@code Event}s
	 * @param listener  the (already registered) event listener
	 * @param predicate the predicate
	 */
	public <F extends Event> void registerListenerPredicate(EventListener<F> listener, Predicate<F> predicate) {
		predicateMap.put(listener, predicate);
	}

	/**
	 * Unregisters the {@link Predicate} of an event listener.
	 * 
	 * @param listener the event listener
	 */
	public void unregisterListenerPredicate(EventListener<?> listener) {
		predicateMap.remove(listener);
	}

	/**
	 * Registers a {@link StateListener}.
	 * 
	 * @param listener the state listener
	 */
	public void register(StateListener listener) {
		stateListeners.add(listener);

		if (getListener(null) == null) {
			installListener(null, new ProcessHandler());
		}

		if (getListener(ActivateEvent.class) == null) {
			installListener(ActivateEvent.class, new ActivateHandler());
			installListener(DeactivateEvent.class, new DeactivateHandler());
			installListener(SuspendEvent.class, new SuspendHandler());
			installListener(ResumeEvent.class, new ResumeHandler());
			installListener(CancelEvent.class, new CancelHandler());
		}
	}

	/**
	 * Unregisters a {@link StateListener}.
	 * 
	 * @param listener the state listener
	 */
	public void unregister(StateListener listener) {
		stateListeners.remove(listener);
	}

	/**
	 * Unregisters all state listeners.
	 */
	public void unregisterStateListeners() {
		stateListeners.clear();
	}

	/**
	 * Unregisters all event and state listeners.
	 */
	public void unregisterAll() {
		unregisterEventListeners();
		unregisterStateListeners();
	}
}

package de.gwasch.code.escframework.events.processors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.gwasch.code.escframework.events.events.ActivateEvent;
import de.gwasch.code.escframework.events.events.Callback;
import de.gwasch.code.escframework.events.events.CallbackAction;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.DeactivateEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.PushAction;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.handler.CallbackListener;
import de.gwasch.code.escframework.events.handler.ProcessListener;



//todo, predecessor iterator
//todo, kein "Handler" in Methodennamen
public abstract class Processor<E extends Event> implements Iterable<Processor<E>> {
		
	class PredecessorIterator implements Iterator<Processor<E>> {

		private Processor<E> cursor = Processor.this;
		
		public boolean hasNext() {
			return cursor.predecessor != null;
		}

		public Processor<E> next() {
			cursor = cursor.predecessor;
			return cursor;
		}
		
	}
	
	private final static String MULTIPLE_SUCCESSORS_MSG = "Function cannot be used in case of multiple successors. Use function with successorIndex instead.";
	
	private static int currentId = 0;
	private int id;
	private String name;
	private Processor<E> predecessor;
	private List<Processor<E>> successors;
	
	private Map<Class<? extends Event>, List<ProcessListener<Event>>> successorsMap;
	private Map<Class<? extends Event>, ProcessListener<Event>> handlerMap;
		
	public Processor(String name) {

		id = currentId++;
		this.name = name;
		
		predecessor = null;
		successors = new ArrayList<>();

		successorsMap = new HashMap<>();
		handlerMap = new HashMap<>();
	}
		
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
		
	public final void addSuccessor(Processor<E> successor) {
		successors.add(successor);
		successor.predecessor = this;
		
		update(successor);
	}
	
	private int getSuccessorsCount(Class<?> eventClass) {
		if (successorsMap.get(eventClass) == null) {
			return 0;
		}
		
		return successorsMap.get(eventClass).size();
	}
	
	private void addSuccessorHandler(Class<? extends Event> eventClass, ProcessListener<? extends Event> handler) {
		
		List<ProcessListener<Event>> handlers = successorsMap.get(eventClass);
		
		if (handlers == null) {
			handlers = new ArrayList<>();
			successorsMap.put(eventClass, handlers);
		}
		
		handlers.add((ProcessListener<Event>)handler);
	}
	
	
	public final <F extends Event> ProcessListener<Event> getHandler(Class<F> eventClass) {
		ProcessListener<Event> listener = (ProcessListener<Event>)handlerMap.get(eventClass);
		return listener;
	}

	//todo, uninstall geht noch nicht
	public final <F extends Event> void installHandler(Class<F> eventClass, ProcessListener<? super F> processHandler) {
				
		handlerMap.put(eventClass, (ProcessListener<Event>)processHandler);

		if (predecessor == null) {
			return;
		}
			
		if (predecessor.getSuccessorsCount(eventClass) == 0) {			// add
			for (Processor<E> predecessor : this) {
				
				if (predecessor.getSuccessorsCount(eventClass) > 0) {
					break;
				}
				
				predecessor.addSuccessorHandler(eventClass, processHandler);
			}			
		}
		else {														// update
			int index = predecessor.successors.indexOf(this);
			ProcessListener<?> oldProcessSuccessor = predecessor.successorsMap.get(eventClass).get(index);
			predecessor.successorsMap.get(eventClass).set(index, (ProcessListener<Event>)processHandler);		
			Processor<E> p = predecessor;
			
			for (Processor<E> predecessor : predecessor) {
				
				index = predecessor.successors.indexOf(p);
				ProcessListener<?> handler = predecessor.successorsMap.get(eventClass).get(index);
				
				if (handler != oldProcessSuccessor) {
					break;
				}
				
				predecessor.successorsMap.get(eventClass).set(index, (ProcessListener<Event>)processHandler);		
				
				p = predecessor;
			}
		}
																	//todo, remove
	}
	
	private void update(Processor<E> successor) {
		
		for (Class<? extends Event> eventClass : successor.handlerMap.keySet()) {
						
			ProcessListener<?> processHandler = successor.handlerMap.get(eventClass);

			for (Processor<E> predecessor = successor.predecessor; predecessor != null; predecessor = predecessor.predecessor) {

				predecessor.addSuccessorHandler(eventClass, processHandler);
				
				if (predecessor.handlerMap.get(eventClass) != null) {
					break;
				}
			}
		}
	}
	
	public final int getSuccessorCount() {
		return successors.size();
	}
	
	public final <F extends Event> void processGeneric(Class<F> eventClass, F event, CallbackListener<? super F> callbackListener) {
		
		if (callbackListener != null) {
			Callback<F> callback = new Callback<>(event, callbackListener);
			event.pushCallback(callback);
		}
		
		if (handlerMap.get(eventClass) == null) {
			forwardGeneric(eventClass, event);
		}
		else {
			handlerMap.get(eventClass).process(event);
		}
	}
	
	public final <F extends Event> void processGeneric(F event, CallbackListener<? super F> callbackListener) {
		processGeneric((Class<F>)event.getClass(), event, callbackListener);
	}
	
	public final <F extends Event> void processGeneric(Class<F> eventClass, F event) {
		processGeneric(eventClass, event, null);
	}
	
	public final <F extends Event> void processGeneric(F event) {
		processGeneric((Class<F>)event.getClass(), event, null);
	}
	
	public final <F extends Event> void forwardGeneric(int successorIndex, Class<F> eventClass, F event, CallbackListener<? super F> callbackListener) {
		
		if (callbackListener != null) {
			Callback<F> callback = new Callback<>(event, callbackListener);
			event.pushCallback(callback);
		}		
		
		if (successorIndex != 0 && (successorIndex >= successors.size() || successorIndex < 0)) {
			throw new IndexOutOfBoundsException();
		}
		
		if (getSuccessorsCount(eventClass) == 0) {
			callbackGeneric(event, true);
		}
		else {
			successorsMap.get(eventClass).get(successorIndex).process(event);
		}
	}
	
	public final <F extends Event> void forwardGeneric(int successorIndex, F event, CallbackListener<? super F> callbackListener) {
		forwardGeneric(successorIndex, (Class<F>)event.getClass(), event, callbackListener);
	}	
	
	public final <F extends Event> void forwardGeneric(int successorIndex, Class<F> eventClass, F event) {
		forwardGeneric(successorIndex, eventClass, event, null);

	}
	
	public final <F extends Event> void forwardGeneric(int successorIndex, F event) {
		forwardGeneric(successorIndex, (Class<F>)event.getClass(), event, null);
	}
	
	public final <F extends Event> void forwardGeneric(Class<F> eventClass, F event, CallbackListener<? super F> callbackListener) {
		
		if (successors.size() > 1) {
			throw new UnsupportedOperationException(MULTIPLE_SUCCESSORS_MSG);
		}
		else {
			forwardGeneric(0, eventClass, event, callbackListener);
		}
	}
	
	public final <F extends Event> void forwardGeneric(F event, CallbackListener<? super F> callbackListener) {
		forwardGeneric((Class<F>)event.getClass(), event, callbackListener);
	}	
	
	public final <F extends Event> void forwardGeneric(Class<F> eventClass, F event) {

		forwardGeneric(eventClass, event, null);
	}
	
	public final <F extends Event> void forwardGeneric(F event) {
		forwardGeneric((Class<F>)event.getClass(), event, null);
	}

	
	public final static <F extends Event> void callbackGeneric(F event, boolean success) {
		callbackGeneric(event, success, null);
	}
	
	public final static <F extends Event> void callbackGeneric(F event, boolean success, PushAction context) {
		
		if (!event.hasCallback()) {
			return;
		}
		
		Callback<?> callback = event.popCallback();
		
		if (context != null) {
			CallbackAction<F> callbackAction = new CallbackAction<F>(callback ,success);
			context.setAction(callbackAction);
			context.execute();
		}
		else {
			callback.callback(success);
		}
	}
	
	public final void process(E event, CallbackListener<? super E> callbackListener) {
		processGeneric(null, event, callbackListener);
	}
	
	public final void process(E event) {
		processGeneric(null, event);
	}

	public final void activate(ActivateEvent event, CallbackListener<ActivateEvent> callbackListener) {
		processGeneric(event, callbackListener);
	}
	
	public final void activate(ActivateEvent event) {
		processGeneric(event);
	}
	
	public final void activate(CallbackListener<ActivateEvent> callbackListener) {
		processGeneric(new ActivateEvent(), callbackListener);
	}
	
	public final void activate() {
		processGeneric(new ActivateEvent());
	}
	
	public final void deactivate(DeactivateEvent event, CallbackListener<DeactivateEvent> callbackListener) {
		processGeneric(event, callbackListener);
	}
	
	public final void deactivate(DeactivateEvent event) {
		processGeneric(event);
	}
	
	public final void deactivate(CallbackListener<DeactivateEvent> callbackListener) {
		processGeneric(new DeactivateEvent(), callbackListener);
	}
	
	public final void deactivate() {
		processGeneric(new DeactivateEvent());
	}
	
	public final void suspend(SuspendEvent event, CallbackListener<SuspendEvent> callbackListener) {
		processGeneric(event, callbackListener);
	}
	
	public final void suspend(SuspendEvent event) {
		processGeneric(event);
	}
	
	public final void suspend(CallbackListener<SuspendEvent> callbackListener) {
		processGeneric(new SuspendEvent(), callbackListener);
	}
	
	public final void suspend() {
		processGeneric(new SuspendEvent());
	}
	
	public final void resume(ResumeEvent event, CallbackListener<ResumeEvent> callbackListener) {
		processGeneric(event, callbackListener);
	}
	
	public final void resume(ResumeEvent event) {
		processGeneric(event);
	}
	
	public final void resume(CallbackListener<ResumeEvent> callbackListener) {
		processGeneric(new ResumeEvent(), callbackListener);
	}
	
	public final void resume() {
		processGeneric(new ResumeEvent());
	}

	public final void cancel(CancelEvent event, CallbackListener<CancelEvent> callbackListener) {
		processGeneric(event, callbackListener);
	}
	
	public final void cancel(CancelEvent event) {
		processGeneric(event);
	}
	
	public final void cancel(CallbackListener<CancelEvent> callbackListener) {
		processGeneric(new CancelEvent(), callbackListener);
	}
	
	public final void cancel() {
		processGeneric(new CancelEvent());
	}

	
	public final void forward(E event, CallbackListener<? super E> callbackListener) {
		forwardGeneric(null, event, callbackListener);
	}
	
	public final void forward(E event) {
		forwardGeneric(null, event);
	}
	
	public final void forward(int successorIndex, E event, CallbackListener<? super E> callbackListener) {
		forwardGeneric(successorIndex, null, event, callbackListener);
	}
	
	public final void forward(int successorIndex, E event) {
		forwardGeneric(successorIndex, null, event);
	}
	public final void forwardActivate(ActivateEvent event, CallbackListener<ActivateEvent> callbackListener) {
		forwardGeneric(event, callbackListener);
	}

	public final void forwardActivate(ActivateEvent event) {
		forwardGeneric(event);
	}
	
	public final void forwardActivate(int successorIndex, ActivateEvent event, CallbackListener<ActivateEvent> callbackListener) {
		forwardGeneric(successorIndex, event, callbackListener);
	}
	
	public final void forwardActivate(int successorIndex, ActivateEvent event) {
		forwardGeneric(successorIndex, event);
	}
	
	public final void forwardDeactivate(DeactivateEvent event, CallbackListener<DeactivateEvent> callbackListener) {
		forwardGeneric(event, callbackListener);
	}

	public final void forwardDeactivate(DeactivateEvent event) {
		forwardGeneric(event);
	}
	
	public final void forwardDeactivate(int successorIndex, DeactivateEvent event, CallbackListener<DeactivateEvent> callbackListener) {
		forwardGeneric(successorIndex, event, callbackListener);
	}
	
	public final void forwardDeactivate(int successorIndex, DeactivateEvent event) {
		forwardGeneric(successorIndex, event);
	}
	
	public final void forwardSuspend(SuspendEvent event, CallbackListener<SuspendEvent> callbackListener) {
		forwardGeneric(event, callbackListener);
	}

	public final void forwardSuspend(SuspendEvent event) {
		forwardGeneric(event);
	}
	
	public final void forwardSuspend(int successorIndex, SuspendEvent event, CallbackListener<SuspendEvent> callbackListener) {
		forwardGeneric(successorIndex, event, callbackListener);
	}
	
	public final void forwardSuspend(int successorIndex, SuspendEvent event) {
		forwardGeneric(successorIndex, event);
	}
	
	public final void forwardResume(ResumeEvent event, CallbackListener<ResumeEvent> callbackListener) {
		forwardGeneric(event, callbackListener);
	}

	public final void forwardResume(ResumeEvent event) {
		forwardGeneric(event);
	}
	
	public final void forwardResume(int successorIndex, ResumeEvent event, CallbackListener<ResumeEvent> callbackListener) {
		forwardGeneric(successorIndex, event, callbackListener);
	}
	
	public final void forwardResume(int successorIndex, ResumeEvent event) {
		forwardGeneric(successorIndex, event);
	}
	
	public final void forwardCancel(CancelEvent event, CallbackListener<CancelEvent> callbackListener) {
		forwardGeneric(event, callbackListener);
	}

	public final void forwardCancel(CancelEvent event) {
		forwardGeneric(event);
	}
	
	public final void forwardCancel(int successorIndex, CancelEvent event, CallbackListener<CancelEvent> callbackListener) {
		forwardGeneric(successorIndex, event, callbackListener);
	}
	
	public final void forwardCancel(int successorIndex, CancelEvent event) {
		forwardGeneric(successorIndex, event);
	}

	
	//todo, callback-funktion müssten in die listener?

	public final void callback(E event, boolean success) {		
		callbackGeneric(event, success);
	}
	
	public final void callback(E event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public final void callback(ActivateEvent event, boolean success) {		
		callbackGeneric(event, success);
	}
	
	public final void callback(ActivateEvent event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public final void callback(DeactivateEvent event, boolean success) {			
		callbackGeneric(event, success);
	}
	
	public final void callback(DeactivateEvent event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public final void callback(SuspendEvent event, boolean success) {			
		callbackGeneric(event, success);
	}
	
	public final void callback(SuspendEvent event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public final void callback(ResumeEvent event, boolean success) {			
		callbackGeneric(event, success);
	}
	
	public final void callback(ResumeEvent event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public final void callback(CancelEvent event, boolean success) {			
		callbackGeneric(event, success);
	}
	
	public final void callback(CancelEvent event, boolean success, PushAction context) {		
		callbackGeneric(event, success, context);
	}
	
	public Iterator<Processor<E>> iterator() {
		return new PredecessorIterator();
	}
	
	public String toString() {
		return getClass().getSimpleName() + " (" + getName() + ", " + getId() + ')';
	}
	
	public String networkToString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Processors: \n");
		
		Processor<?> p = this;
		
		do {
			assert p.getSuccessorCount() <= 1;
			sb.append(" - ");
			sb.append(p);
			sb.append("\n   - Handler: ");
			
			for (Class<?> eventClass : p.handlerMap.keySet()) {
				String handlerName = p.handlerMap.get(eventClass).toString();
				handlerName = handlerName.substring(46, handlerName.length());
				sb.append(handlerName + ", ");
			}
			
			sb.append("\n   - Successors: ");

			for (Class<?> eventClass : p.successorsMap.keySet()) {
				assert p.successorsMap.get(eventClass).size() == 1;
				String successorName = p.successorsMap.get(eventClass).get(0).toString();
				successorName = successorName.substring(46, successorName.length());
				sb.append(successorName + ", ");
			}
			
			sb.append("\n");
			
			if (p.getSuccessorCount() == 0) {
				break;
			}
			else {
				p = p.successors.get(0);
			}
		} while (true);
		
		return sb.toString();
	}
}

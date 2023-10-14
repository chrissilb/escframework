package de.gwasch.code.escframework.events.processors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import de.gwasch.code.escframework.events.events.ActivateEvent;
import de.gwasch.code.escframework.events.events.Callback;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.DeactivateEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.events.TimerAction;
import de.gwasch.code.escframework.events.handler.CallbackListener;
import de.gwasch.code.escframework.events.handler.MergeCallbackHandler;
import de.gwasch.code.escframework.events.handler.ProcessListener;
import de.gwasch.code.escframework.events.utils.TimerFactory;
import de.gwasch.code.escframework.utils.logging.Logger;

//todo, der scheduler geht davon aus, dass jeder successor nur ein event verarbeiten kann

/**
 * A {@code Scheduler} buffers incoming events in a {@link Queue}. By default those events are handled FIFO. 
 * Although a {@code Scheduler} can be parameterized by a {@link PriorityQueue}. In this case events are treated 
 * according the implementation of the {@link Comparable} interface. For example, this is used by the timer processing network
 * (see {@link TimerFactory}) to process {@link TimerAction}s according to their {@link Event#getPushTime()}.
 * <p>
 * In opposite to all other standard event processors a {@code Scheduler} can have multiple successor processors.
 * {@link Callback}s of successors are collected to give common feedback to predecessors.
 * <p>
 * {@code Scheduler}s must be activated once all successors are defined.
 * 
 * @param <E> The event type considered by the {@code Scheduler}.
 */
public class Scheduler<E extends Event> extends Processor<E> {

	class CallbackHandler implements CallbackListener<E> {

		private int successorIndex;
		
		public CallbackHandler(int successorIndex) {
			this.successorIndex = successorIndex;
		}
		
		public void finish(E event, boolean success) {
						
			Logger.log("finish: " + success, Scheduler.this, event);
			
//			if (getName().equals("timer")) {
//				System.out.println("finish: " + event + ", " + success);
//			}
			
			activeEvents.set(successorIndex, null);
			nrActive--;
			
			boolean doCallback = true;
			
			//NOTE: cancelled events cannot be informed before this because they might be 
			//      "finished" already in the worker thread and wait in an event queue.
			if (cancelEvents[successorIndex]) {
				success = false;				
			}
			else if (suspendEvents[successorIndex]) {
				suspendedEvents.add(event);
				doCallback = false;
			}
			else if (suppressEvents[successorIndex]) {
				queue.offer(event);
				doCallback = false;
			}

			
			cancelEvents[successorIndex] = false;
			suppressEvents[successorIndex] = false;
			suspendEvents[successorIndex] = false;
			
			if (doCallback) {
				callback(event, success);
			}
			
			schedule();
		}
	}
		
	class ProcessHandler implements ProcessListener<E> {
				
		public void process(E event) {
			
//			if (getName().equals("timer")) {
//				System.out.println("add: " + event);
//				System.out.print("");
//			}
//			assert SwingUtilities.isEventDispatchThread();
			Logger.log("process", Scheduler.this, event);

			//todo, mergen anbieten, aber dann muss ggf. neue enqueued werden, da sich die Reihenfolge ändern kann
			
			if (isSuspended) {
				event.suspend();
				suspendedEvents.add(event);
			}
			else {
				queue.offer(event);
				schedule();
			}
		}
	}
	
	class ActivateHandler implements ProcessListener<ActivateEvent> {

		private MergeCallbackHandler<ActivateEvent> activateCallbackHandler;
		
		public void process(ActivateEvent event) {
			assert activateCallbackHandler == null || activateCallbackHandler.getNrRemainingCallbacks() == 0;
			
			int nrSuccessors = getSuccessorCount();
			assert nrSuccessors > 0;
			
			activeEvents = new ArrayList<>(nrSuccessors);
			cancelEvents = new boolean[nrSuccessors];
			suspendEvents = new boolean[nrSuccessors];
			suppressEvents = new boolean[nrSuccessors];
			processCallbackHandlers = new ArrayList<>(nrSuccessors);
			activateCallbackHandler = new MergeCallbackHandler<>(nrSuccessors, event);
			
			for (int i = 0; i < nrSuccessors; i++) {
				activeEvents.add(null);
				processCallbackHandlers.add(new CallbackHandler(i));
				//NOTE: clone() is necessary because of different callback chains
				forwardActivate(i, event.clone(), activateCallbackHandler);
			}
		}
	}
	
	class DeactivateHandler implements ProcessListener<DeactivateEvent> {
		
		private MergeCallbackHandler<DeactivateEvent> deactivateCallbackHandler;
		
		public void process(DeactivateEvent event) {
			int nrSuccessors = getSuccessorCount();
			deactivateCallbackHandler = new MergeCallbackHandler<>(nrSuccessors, event);
			
			for (int i = 0; i < nrSuccessors; i++) {
				//NOTE: clone() is necessary because of different callback chains
				forwardDeactivate(i, event.clone(), deactivateCallbackHandler);
			}
		}
	}
	
	class SuspendHandler implements ProcessListener<SuspendEvent> {
		
		private MergeCallbackHandler<SuspendEvent> suspendCallbackHandler;
		
		public void process(SuspendEvent event) {
			
			boolean suspendForwarded = false;
			
			if (event.getPatternEvent() == null) {
				isSuspended = true;
				
				for (E e : queue) {
					e.suspend();
				}
				
				suspendedEvents.addAll(queue);
				queue.clear();
				
				if (nrActive > 0) {
					suspendCallbackHandler = new MergeCallbackHandler<>(nrActive, event);
					
					for (int i = 0; i < activeEvents.size(); i++) {
						if (activeEvents.get(i) != null) {
							suspendEvents[i] = true;
							activeEvents.get(i).suspend();
							//NOTE: clone() is necessary because of different callback chains
							forwardSuspend(i, event.clone(), suspendCallbackHandler);
						}
					}
					
					suspendForwarded = true;
				}
			}
			else {
				Event wrappedEvent = event.getPatternEvent();
				
				for (Iterator<E> it = queue.iterator(); it.hasNext();) {
					E e = it.next();
					if (wrappedEvent.equals(e)) {
						e.suspend();
						suspendedEvents.add(e);
						it.remove();
					}
				}
				
				List<Integer> suspendEventIndexes = new ArrayList<>();
				for (int i = 0; i < activeEvents.size(); i++) {
					E e = activeEvents.get(i);
					if (wrappedEvent.equals(e)) {
						wrappedEvent.suspend();
						suspendEventIndexes.add(i);
					}
				}
				
				if (suspendEventIndexes.size() > 0) {
					suspendCallbackHandler = new MergeCallbackHandler<>(suspendEventIndexes.size(), event);
					for (Integer i : suspendEventIndexes) {
						suspendEvents[i] = true;
						//NOTE: clone() is necessary because of different callback chains
						forwardSuspend(i, event.clone(), suspendCallbackHandler);
					}
					
					suspendForwarded = true;
				}
			}				
			
			if (!suspendForwarded) {
				callback(event, true);
			}
		}
	}
	
	class ResumeHandler implements ProcessListener<ResumeEvent> {
		
		private MergeCallbackHandler<ResumeEvent> resumeCallbackHandler;
		
		public void process(ResumeEvent event) {
			
			int nrSuccessors = getSuccessorCount();
			resumeCallbackHandler = new MergeCallbackHandler<>(nrSuccessors, event);
			
			for (int i = 0; i < nrSuccessors; i++) {
				//NOTE: clone() is necessary because of different callback chains
				forwardResume(i, event.clone(), resumeCallbackHandler);
			}
			
			boolean foundSuspendedEvent = false;
			
			if (event.getPatternEvent() == null) {
				isSuspended = false;
			
				if (suspendedEvents.size() > 0) {
					for (E e : suspendedEvents) {
						e.resume();
					}
					
					queue.addAll(suspendedEvents);
					suspendedEvents.clear();
					foundSuspendedEvent = true;
				}
			}
			else {
				Event wrappedEvent = event.getPatternEvent();
				
				for (Iterator<E> it = suspendedEvents.iterator(); it.hasNext();) {
					E e = it.next();
					
					if (wrappedEvent.equals(e)) {
						it.remove();
						e.resume();
						queue.offer(e);
						foundSuspendedEvent = true;
					}
				}
			}
			
			if (foundSuspendedEvent) {
				schedule();
			}
		}
	}
		
	class CancelHandler implements ProcessListener<CancelEvent> {
		
		private MergeCallbackHandler<CancelEvent> cancelCallbackHandler;

		public void process(CancelEvent event) {
			
			assert cancelCallbackHandler == null || cancelCallbackHandler.getNrRemainingCallbacks() == 0;

			List<E> callbackEvents = new ArrayList<>();
			boolean cancelForwarded = false;
			
			if (event.getPatternEvent() == null) {
				callbackEvents.addAll(queue);
				queue.clear();
				callbackEvents.addAll(suspendedEvents);
				suspendedEvents.clear();
				
				
				if (nrActive > 0) {
					cancelCallbackHandler = new MergeCallbackHandler<>(nrActive, event);
					
					for (int i = 0; i < activeEvents.size(); i++) {
						if (activeEvents.get(i) != null) {
							cancelEvents[i] = true;
							//NOTE: clone() is necessary because of different callback chains
							forwardCancel(i, event.clone(), cancelCallbackHandler);
						}
					}
					
					cancelForwarded = true;
				}
			}
			else {
				Event wrappedEvent = event.getPatternEvent();
				
//				if (getName().equals("timer")) {
//					System.out.println("cancel: " + wrappedEvent);
//				}
				
				for (Iterator<E> it = queue.iterator(); it.hasNext();) {
					E e = it.next();
					if (wrappedEvent.equals(e)) {
						it.remove();
						callbackEvents.add(e);
					}
				}
				
				for (Iterator<E> it = suspendedEvents.iterator(); it.hasNext();) {
					E e = it.next();
					if (wrappedEvent.equals(e)) {
						it.remove();
						callbackEvents.add(e);
					}
				}
	
				List<Integer> cancelEventIndexes = new ArrayList<>();
				for (int i = 0; i < activeEvents.size(); i++) {
					E e = activeEvents.get(i);
					if (wrappedEvent.equals(e)) {
						cancelEventIndexes.add(i);
					}
				}
				
				if (cancelEventIndexes.size() > 0) {
					cancelCallbackHandler = new MergeCallbackHandler<>(cancelEventIndexes.size(), event);
					for (Integer i : cancelEventIndexes) {
						cancelEvents[i] = true;
						//NOTE: clone() is necessary because of different callback chains
						forwardCancel(i, event.clone(), cancelCallbackHandler);
					}
					
					cancelForwarded = true;
				}
			}
			
			for (E e : callbackEvents) {
				callback(e, false);
			}
			
			if (!cancelForwarded) {
				callback(event, true);
			}
		}
	}
		
	private List<CallbackListener<E>> processCallbackHandlers;
	
	private Queue<E> queue;
	private List<E> activeEvents;
	private int nrActive;
	private boolean[] cancelEvents;
	private boolean[] suppressEvents;
	private boolean isSuspended;
	private boolean[] suspendEvents;
	private List<E> suspendedEvents;

	//todo, suspendEvents so wie cancelEvents berücksichtigen
	
	private boolean suppress;
	
	public Scheduler(String name, Queue<E> queue, boolean suppress) {	
		super(name);
		
		this.queue = queue;
		activeEvents = null;
		nrActive = 0;
		cancelEvents = null;
		suppressEvents = null;
		this.suppress = suppress;
		isSuspended = false;
		suspendEvents = null;
		suspendedEvents = new ArrayList<>();
		processCallbackHandlers = null;
		
		installHandler(null, new ProcessHandler());
		installHandler(ActivateEvent.class, new ActivateHandler());
		installHandler(DeactivateEvent.class, new DeactivateHandler());
		installHandler(SuspendEvent.class, new SuspendHandler());
		installHandler(ResumeEvent.class, new ResumeHandler());
		installHandler(CancelEvent.class, new CancelHandler());
	}
	
	public Scheduler() {
		this("");
	}
	
	public Scheduler(Queue<E> queue, boolean squeeze) {
		this("", queue, squeeze);
	}
	
	public Scheduler(String name) {
		this(name, new LinkedList<E>(), false);
	}
			
	//NOTE: this method can be called recursively. Thus, "forward" methods shall be called before leaving the function.
	private void schedule() {
	
		if (queue.isEmpty()) {
			return;
		}
		
		if (nrActive < getSuccessorCount()) {
			int i = 0;
			for (; i < activeEvents.size(); i++) {
				if (activeEvents.get(i) == null) {
					break;
				}
			}
			
			E event = queue.poll();
			
			//todo, durch das mergen "verschwindet" ein Event. Es muss aber als Callback berücksichtigt werden.
			// Lösung es könnte das alte mit success = false zurückgeliefert werden
			// todo, nur mergen alleine reicht wohl nicht. Was passiert mit der Action-Ausführung? Neu einstellen?
			
			activeEvents.set(i, event);	
			Logger.log("schedule", Scheduler.this, event);
			nrActive++;

//			if (getName().equals("timer")) {
//				System.out.println("process: " + event);
//			}

			forward(i, event, processCallbackHandlers.get(i));
		}
		else if (suppress) {
			//todo, der schwächste muss verdrängt werden?
			
			
			for (int i = 0; i < cancelEvents.length; i++) {
				if (cancelEvents[i] || suspendEvents[i] || suppressEvents[i]) {
					return;
				}
			}
			
			int index = 0;
			for (; index < activeEvents.size(); index++) {
				if (queue.peek().compareTo(activeEvents.get(index)) < 0) {
					break;
				}
			}

			if (index < activeEvents.size() && cancelEvents[index] == false) {
				Logger.log("schedule: " + activeEvents.get(index) + " suppressed.", Scheduler.this, queue.peek());
				System.out.println(getName() + " schedule: " + activeEvents.get(index) + " suppressed by " + queue.peek() + ".");
				suppressEvents[index] = true;
				forwardCancel(index, new CancelEvent());
			}
		}
	}
}



package de.gwasch.code.escframework.events.processors;

import de.gwasch.code.escframework.events.events.Callback;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.events.TimerAction;
import de.gwasch.code.escframework.events.listeners.CallbackListener;
import de.gwasch.code.escframework.events.listeners.MergeCallbackHandler;
import de.gwasch.code.escframework.events.listeners.ProcessListener;
import de.gwasch.code.escframework.events.utils.TimerFactory;
import de.gwasch.code.escframework.utils.logging.Logger;

/**
 * A {@code TimerHandler} redirects events to the timer processor network if their push time is in the future (see {@link TimerFactory}).
 * Corresponding {@link Callback}s are collected and forwarded to the actual processor network.
 * 
 * @param <E> the event type considered by the {@code TimerHandler}
 */
public class TimerHandler<E extends Event> extends Processor<E> {
	
	class CallbackHandler implements CallbackListener<TimerAction<E>> {

		public void finish(TimerAction<E> timerAction, boolean success) {

//			assert SwingUtilities.isEventDispatchThread();
			Logger.log("finish", TimerHandler.this, timerAction);

			if (success) {
				forward(timerAction.getEvent());
			}
			else {
				callback(timerAction.getEvent(), success);
			}
		}
	}
	
	class ProcessHandler implements ProcessListener<E> {
		
		public void process(E event) {

//			assert SwingUtilities.isEventDispatchThread();
			Logger.log("process", TimerHandler.this, event);
			
			if (event.getPushTime() > System.currentTimeMillis()) {
				TimerAction<E> timerAction = new TimerAction<E>(event);
				timer.process(timerAction, timerCallbackHandler);
				return;
			}
			
			forward(event);
		}
	}
	
	class SuspendHandler implements ProcessListener<SuspendEvent> {

		private MergeCallbackHandler<SuspendEvent> suspendCallbackHandler;
		
		public void process(SuspendEvent event) {
			assert suspendCallbackHandler == null || suspendCallbackHandler.getNrRemainingCallbacks() == 0;

			suspendCallbackHandler = new MergeCallbackHandler<SuspendEvent>(2, event);
			
			TimerAction<Event> timerAction = new TimerAction<>(event.getPatternEvent());
			SuspendEvent timerSuspendEvent = new SuspendEvent(timerAction);
			timer.suspend(timerSuspendEvent, suspendCallbackHandler);
			forwardSuspend(event, suspendCallbackHandler);
		}
	}
	
	class ResumeHandler implements ProcessListener<ResumeEvent> {

		private MergeCallbackHandler<ResumeEvent> resumeCallbackHandler;
				
		public void process(ResumeEvent event) {
			assert resumeCallbackHandler == null || resumeCallbackHandler.getNrRemainingCallbacks() == 0;

			resumeCallbackHandler = new MergeCallbackHandler<ResumeEvent>(2, event);
			
			TimerAction<Event> timerAction = new TimerAction<>(event.getPatternEvent());
			ResumeEvent timerResumeEvent = new ResumeEvent(timerAction);
			timer.resume(timerResumeEvent, resumeCallbackHandler);
			forwardResume(event, resumeCallbackHandler);
		}
	}
	
	class CancelHandler implements ProcessListener<CancelEvent> {

		private MergeCallbackHandler<CancelEvent> cancelCallbackHandler;

		public void process(CancelEvent event) {
			assert cancelCallbackHandler == null || cancelCallbackHandler.getNrRemainingCallbacks() == 0;

			cancelCallbackHandler = new MergeCallbackHandler<CancelEvent>(2, event);
			
			TimerAction<Event> timerAction = new TimerAction<>(event.getPatternEvent());
			CancelEvent timerCancelEvent = new CancelEvent(timerAction);
			timer.cancel(timerCancelEvent, cancelCallbackHandler);
			forwardCancel(event, cancelCallbackHandler);
		}
	}
	
	private Processor<TimerAction<E>> timer;
	private CallbackListener<TimerAction<E>> timerCallbackHandler;
	
	public TimerHandler(String name, Processor<TimerAction<E>> timer) {
		super(name);
		
		this.timer = timer;
		timerCallbackHandler = new CallbackHandler();
		
		installHandler(null, new ProcessHandler());
//		installHandler(ActivateEvent.class, new ActivateHandler());
//		installHandler(DeactivateEvent.class, new DeactivateHandler());
		installHandler(SuspendEvent.class, new SuspendHandler());
		installHandler(ResumeEvent.class, new ResumeHandler());
		installHandler(CancelEvent.class, new CancelHandler());
	}
	
	public TimerHandler(Processor<TimerAction<E>> timer) {
		this("", timer);
	}
}

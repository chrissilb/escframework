package de.gwasch.code.escframework.events.processors;

import javax.swing.SwingUtilities;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.ResumeEvent;
import de.gwasch.code.escframework.events.events.SuspendEvent;
import de.gwasch.code.escframework.events.handler.CallbackListener;
import de.gwasch.code.escframework.events.handler.ProcessListener;
import de.gwasch.code.escframework.events.sources.EventSource;

/**
 * An {@code Inlet} generates events based on an {@link EventSource}. It starts generating on resume and stops on suspend.
 * 
 * @param <E> the event type considered by the {@code Inlet}
 */
public class Inlet<E extends Event> extends Processor<E> {
	
	class CallbackHandler implements CallbackListener<E> {

		public void finish(E event, boolean success) {		
			
			assert SwingUtilities.isEventDispatchThread();
			
			if (event == currentEvent) {
				if (!isSuspended) {
					generate(!success);
				}				
			}
			
			callback(event, success);
		}
	}
	
	class ResumeHandler implements ProcessListener<ResumeEvent> {
		
		public void process(ResumeEvent event) {
			if (isSuspended) {		
				isSuspended = false;
				generate(true);
			}
			
			forwardResume(event);
		}
	}
	
	class SuspendHandler implements ProcessListener<SuspendEvent> {
		
		public void process(SuspendEvent event) {
			isSuspended = true;
			
			forwardSuspend(event);
		}
	}
	
	private CallbackListener<E> callbackHandler;

	private boolean isSuspended;
	private EventSource<? extends E> source;
	private E currentEvent;
	
	public Inlet(String name, EventSource<? extends E> source) {
		super(name);
		
		this.source = source;
		isSuspended = true;
		currentEvent = null;
		callbackHandler = new CallbackHandler();
		
		installHandler(SuspendEvent.class, new SuspendHandler());
		installHandler(ResumeEvent.class, new ResumeHandler());
	}

	public Inlet(EventSource<? extends E> source) {
		this("", source);
	}
	
	public EventSource<? extends E> getSource() {
		return source;
	}
	
	private void generate(boolean immediately) {
		assert SwingUtilities.isEventDispatchThread();
		
		currentEvent = source.pop();
		if (immediately) {
			currentEvent.setPushTime(0);
		}
		forward(currentEvent, callbackHandler);
	}
}

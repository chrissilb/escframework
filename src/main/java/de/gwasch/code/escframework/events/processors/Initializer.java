package de.gwasch.code.escframework.events.processors;


import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.handler.CallbackListener;
import de.gwasch.code.escframework.events.handler.ProcessListener;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.utils.logging.Logger;


/**
 * {@code Initializer} invokes {@link Event#setId}, {@link Event#setPushTime} and {@link Event#setPopTime} if corresponding 
 * attributes are not set, yet. For example, this is useful so that {@link PatternMatcher} knows an Event is new or old or to
 * measure how much time an event needed to be processed by the event network.
 * 
 * @param <E> the event type considered by the {@code Initializer}
 */
public class Initializer<E extends Event> extends Processor<E> {
	
	class CallbackHandler implements CallbackListener<E> {

		public void finish(E event, boolean success) {

//			assert SwingUtilities.isEventDispatchThread();
			
			if (event.getPopTime() == 0) {
				event.setPopTime(System.currentTimeMillis());
			}
			
			Logger.log("finish", Initializer.this, event);
			
			callback(event, success);
		}
	}
	
	class ProcessHandler implements ProcessListener<E> {
		
		public void process(E event) {
			
//			assert SwingUtilities.isEventDispatchThread();
			
			if (event.getId() == -1) {
				event.setId(lastEventId++);
			}
			if (event.getPushTime() == 0) {
				event.setPushTime(System.currentTimeMillis());
			}

			Logger.log("process", Initializer.this, event);

			forward(event, callbackHandler);
		}
	}
	
	private CallbackListener<E> callbackHandler;
	
	private int lastEventId;
	
	public Initializer(String name, int offset) {
		super(name);
		
		lastEventId = offset;
		callbackHandler = new CallbackHandler();
		
		installHandler(null, new ProcessHandler());
	}

	public Initializer() {
		this("", 0);
	}
	public Initializer(String name) {
		this(name, 0);
	}
	
	public Initializer(int offset) {
		this("", offset);
	}
}

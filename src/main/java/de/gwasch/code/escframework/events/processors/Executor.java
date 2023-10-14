package de.gwasch.code.escframework.events.processors;


import javax.swing.SwingUtilities;

import de.gwasch.code.escframework.events.events.Action;
import de.gwasch.code.escframework.events.events.CancelEvent;
import de.gwasch.code.escframework.events.listeners.ProcessListener;
import de.gwasch.code.escframework.utils.logging.Logger;

/**
 * {@code Executor} executes {@link Action}s. It is the only standard processor that cannot operate on events but on actions.
 * 
 * @param <A> the action type considered by the {@code Executor}
 */
public class Executor<A extends Action> extends Processor<A> {
	
	class ProcessHandler implements ProcessListener<A> {
		
		public void process(A action) {
			Logger.log("process", Executor.this, action);
			
			assert !SwingUtilities.isEventDispatchThread();

			currentAction = action;
			boolean success = action.execute();		
			currentAction = null;
			callback(action, success);
		}
	}
			
	class CancelHandler implements ProcessListener<CancelEvent> {
		
		public void process(CancelEvent event) {
						
			if (currentAction != null) {
				if (event.getPatternEvent() == null || event.getPatternEvent().equals(currentAction)) {
					currentAction.cancel();
				}
			}
			
			forwardCancel(event);
		}
	}
	

	private A currentAction;
	
	public Executor(String name) {
		super(name);
		currentAction = null;
		
		installHandler(null, new ProcessHandler());
//		setHandler(ActivateEvent.class, new ActivateHandler());
//		setHandler(DeactivateEvent.class, new DeactivateHandler());
//		setHandler(SuspendEvent.class, new SuspendHandler());
//		setHandler(ResumeEvent.class, new ResumeHandler());
		installHandler(CancelEvent.class, new CancelHandler());
	}
	
	public Executor() {
		this("");
	}
}

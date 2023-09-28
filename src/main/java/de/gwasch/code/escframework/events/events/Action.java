package de.gwasch.code.escframework.events.events;

/**
 * {@code Action} is the main interface of all actions.
 * <p>
 * An {@code Action} must be reusable, e.g. for reenqueuing in {@link Scheduler}. 
 * That means that after {@link #cancel()} the {@link #execute()} method can be called again.
 */

public interface Action extends Event {

	boolean execute();	
	void cancel();
	
	Action getUndoAction();
}

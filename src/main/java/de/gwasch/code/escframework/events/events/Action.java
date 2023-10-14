package de.gwasch.code.escframework.events.events;

import de.gwasch.code.escframework.events.processors.Scheduler;

/**
 * {@code Action} is the main interface of all actions. Actions are executable {@link Event}s.
 * <p>
 * An {@code Action} must be reusable, e.g. for reenqueuing in {@link Scheduler}. 
 * That means that after {@link #cancel()} the {@link #execute()} method can be called again.
 */
public interface Action extends Event {

	/**
	 * Executes the {@code Action}.
	 * @return True if successful, false if failed
	 */
	boolean execute();	
	
	/**
	 * Cancels the execution of an {@code Action}.
	 */
	void cancel();
	
	/** 
	 * Provides an {@code Action} to undo this {@code Action}.
	 * @return The undo action.
	 */
	Action getUndoAction();
}

package de.gwasch.code.escframework.events.events;

// NOTE: An Action must be reusable, e.g. for reenqueuing in Scheduler. 
//  That means that after cancel() the execute() method can be called again.
public interface Action extends Event {

	boolean execute();	
	void cancel();
	
	Action getUndoAction();
}

package de.gwasch.code.escframework.events.events;

/** 
 * An abstract adapter class. The methods in this class are empty. 
 * It exists as convenience for creating concrete actions.
 */
public abstract class AbstractAction extends AbstractEvent implements Action {

	public void cancel() {
	}

	public void suspend() {	
	}
	
	public void resume() {
	}
	
	
	public Action getUndoAction() {
		return null;
	}
}

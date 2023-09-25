package de.gwasch.code.escframework.events.events;

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

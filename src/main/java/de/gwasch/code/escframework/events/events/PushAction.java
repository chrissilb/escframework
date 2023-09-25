package de.gwasch.code.escframework.events.events;

// NOTE: must be cloned/unique per AbstractHandler type.

public interface PushAction extends Action, Cloneable {
	
	PushAction clone();
	Action getAction();
	void setAction(Action action);
}

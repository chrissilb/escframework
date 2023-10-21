package de.gwasch.code.escframework.events.events;

/**
 * A {@code PushAction} contains another {@link Action} and executes it.
 * It is used to execute a {@link CallbackAction} within the caller thread.
 * In this context a {@code PushAction} shall be clone if reused. 
 */
public interface PushAction extends Action, Cloneable {	
	
	/**
	 * Clones a {@code PushAction}.
	 * @return the cloned {@code PushAction}
	 */
	PushAction clone();
	
	/**
	 * Returns its action.
	 * @return the contained action
	 */
	Action getAction();
	
	/**
	 * Sets the action.
	 * @param action the action
	 */
	void setAction(Action action);
}

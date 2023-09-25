package de.gwasch.code.escframework.events.events;

public class CallbackAction<E extends Event> extends AbstractAction {
		
	private Callback<?> callback;
	private boolean success;
	
	public CallbackAction(Callback<?> callback, boolean success) {
		this.callback = callback;
		this.success = success;
	}
	
	public boolean execute() {
		callback.callback(success);
		return true;
	}
}
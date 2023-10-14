package de.gwasch.code.escframework.events.listeners;

import de.gwasch.code.escframework.events.processors.Dispatcher;

/**
 * The {@code StateListener} allows to register for state events at the {@link Dispatcher}.
 * 
 * @see Dispatcher
 * @see StateAdapter
 */
public interface StateListener {
	void onActivate();
	void onActivated(boolean success);
	void onDeactivate();
	void onDeactivated(boolean success);
	void onSuspend();
	void onSuspended(boolean success);
	void onResume();
	void onResumed(boolean success);
	void onCancel();
	void onCancelled(boolean success);
	void onWorking();
	void onIdle();
}
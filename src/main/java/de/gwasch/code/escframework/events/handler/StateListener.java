package de.gwasch.code.escframework.events.handler;

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
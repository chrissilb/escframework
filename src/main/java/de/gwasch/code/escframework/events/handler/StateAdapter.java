package de.gwasch.code.escframework.events.handler;

public abstract class StateAdapter implements StateListener {

	public void onActivate() {
	}

	public void onActivated(boolean success) {
	}
	
	public void onDeactivate() {
	}

	public void onDeactivated(boolean success) {
	}

	public void onSuspend() {
	}

	public void onSuspended(boolean success) {
	}

	public void onResume() {
	}

	public void onResumed(boolean success) {
	}

	public void onCancel() {
	}
	
	public void onCancelled(boolean success) {
	}
	
	public void onWorking() {
	}
	
	public void onIdle() {
	}
}

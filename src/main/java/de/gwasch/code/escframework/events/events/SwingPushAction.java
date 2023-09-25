package de.gwasch.code.escframework.events.events;

import javax.swing.SwingUtilities;

public class SwingPushAction extends AbstractPushAction {
	
	public SwingPushAction() {
		super(null);
	}
	
	public SwingPushAction(Action action) {
		super(action);
	}
	
	public SwingPushAction clone() {
		SwingPushAction clone = new SwingPushAction();
		return clone;
	}

	public boolean execute() {
		assert !SwingUtilities.isEventDispatchThread();
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
				getAction().execute();
			}
		});
		
		return true;
	}
}

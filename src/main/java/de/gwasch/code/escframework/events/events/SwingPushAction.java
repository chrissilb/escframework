package de.gwasch.code.escframework.events.events;

import javax.swing.SwingUtilities;

import de.gwasch.code.escframework.events.processors.ManagedThread;

/**
 * {@code SwingPushAction} executes its action within the Swing's <a href=
 * "https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html">Event
 * Dispatch Thread</a>.
 * 
 * @see ManagedThread
 */
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

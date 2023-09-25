package de.gwasch.code.escframework.events.events;

import java.util.concurrent.BlockingQueue;

public class MainPushAction extends AbstractPushAction {
	
	private BlockingQueue<Runnable> queue;

	public MainPushAction(BlockingQueue<Runnable> queue) {
		this(queue, null);
	}

	public MainPushAction(BlockingQueue<Runnable> queue, Action action) {
		super(action);
		this.queue = queue;
	}
	
	public MainPushAction clone() {
		MainPushAction clone = new MainPushAction(queue);
		return clone;
	}
	
	public boolean execute() {

		queue.add(new Runnable() {
			public void run() {
				getAction().execute();
			}
		});
				
		return true;
	}
}

package de.gwasch.code.escframework.events.events;

import java.util.concurrent.BlockingQueue;

import de.gwasch.code.escframework.events.processors.ManagedThread;

/**
 * {@code MainPushAction} executes its action within Java's main thread. In
 * order to use this a {@link BlockingQueue} must be provided and within the
 * main thread corresponding feedback must be considered. Here is an example how
 * it can work:
 * 
 * <pre>
 * public class App5 {
 *
 * 	static class InitStateHandler extends StateAdapter {
 * 
 * 		public void onIdle() {
 * 			p.deactivate();
 * 		}
 *
 * 		public void onDeactivated(boolean success) {
 * 			System.out.println("worker threads deactivated");
 * 			timer.deactivate();
 * 		}
 * 	}
 *
 * 	static class TimerStateHandler extends StateAdapter {
 *
 * 		public void onDeactivated(boolean success) {
 * 			System.out.println("timer thread deactivated");
 * 			queue.add(new DeactivateRunnable());
 * 		}
 * 	}
 * 
 * 	private static BlockingQueue&lt;Runnable&gt; queue;
 * 	private static Dispatcher&lt;TimerAction&lt;WorkAction&gt;&gt; timer;
 * 	private static Processor&lt;WorkAction&gt; p;
 * 
 * 	public static void main(String[] args) throws InterruptedException {
 * 
 * 		queue = new LinkedBlockingQueue&lt;&gt;();
 * 		timer = TimerFactory.createTimer(new MainPushAction(queue));
 * 		timer.register(new TimerStateHandler());
 * 		timer.activate();
 * 
 * 		Dispatcher&lt;WorkAction&gt; dispatcher;
 * 		Scheduler&lt;WorkAction&gt; scheduler;
 * 
 * 		PNBuilder&lt;WorkAction&gt; pnBuilder = new PNBuilder&lt;WorkAction&gt;("init").add(dispatcher = new Dispatcher&lt;&gt;())
 * 				.add(new TimerHandler&lt;&gt;(timer)).add(scheduler = new Scheduler&lt;&gt;());
 *
 * 		for (int i = 0; i &lt; 2; i++) {
 * 			pnBuilder.add(scheduler, new ManagedThread&lt;&gt;(new MainPushAction(queue))).add(new Executor&lt;&gt;());
 * 		}
 * 
 * 		dispatcher.register(new InitStateHandler());
 *
 * 		p = pnBuilder.top();
 * 
 * 		p.activate();
 * 
 * 		for (int i = 0; i &lt; 10; i++) {
 * 			p.process(new WorkAction("wp1"));
 * 		}
 * 		p.process(new WorkAction("wp2", System.currentTimeMillis() + 2000));
 * 		p.process(new WorkAction("wp3", System.currentTimeMillis() + 2000));
 *
 * 		while (true) {
 * 			Runnable r = queue.take();
 *
 * 			if (r instanceof DeactivateRunnable) {
 * 				break;
 * 			} else {
 * 				r.run();
 * 			}
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * This sample is taken from <a href=
 * "https://github.com/chrissilb/demothreadpool">https://github.com/chrissilb/demothreadpool</a>.
 * 
 * @see ManagedThread
 */
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

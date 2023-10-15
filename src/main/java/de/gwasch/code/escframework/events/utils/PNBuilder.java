package de.gwasch.code.escframework.events.utils;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

/**
 * {@code PNBuilder} ("processor network builder") is used to build a processor network. Its implementation follows 
 * the builder design pattern.
 * <p>
 * Example:
 * <pre>
 * private static BlockingQueue&lt;Runnable&gt; queue;
 * private static Dispatcher&lt;TimerAction&lt;WorkAction&gt;&gt; timer;
 * private static Processor&lt;WorkAction&gt; p;
 * 
 * public static void main(String[] args) throws InterruptedException {
 * 
 *     queue = new LinkedBlockingQueue&lt;&gt;();
 *     timer = TimerFactory.createTimer(new MainPushAction(queue));
 *     timer.register(new TimerStateHandler());
 *     timer.activate();
 *     
 *     Dispatcher&lt;WorkAction&gt; dispatcher;
 *     Scheduler&lt;WorkAction&gt; scheduler;
 *     
 *     PNBuilder&lt;WorkAction&gt; pnBuilder = new PNBuilder&lt;WorkAction&gt;("init")
 *         .add(dispatcher = new Dispatcher&lt;&gt;())
 *         .add(new TimerHandler&lt;&gt;(timer))
 *         .add(scheduler = new Scheduler&lt;&gt;());
 *         
 *     for (int i = 0; i &lt; 2; i++) {
 *         pnBuilder.add(scheduler, new ManagedThread&lt;&gt;(new MainPushAction(queue))).add(new Executor&lt;&gt;());
 *     }
 *     
 *     dispatcher.register(new InitStateHandler());
 *     p = pnBuilder.top();
 *     p.activate();
 * } 
 * </pre>
 * This code snippet creates a simple thread pool. Complete examples can be found 
 * <a href="https://github.com/chrissilb/demothreadpool">here</a>.
 * 
 * @param <E> the event type considered by the {@code PNBuilder}
 */
public class PNBuilder<E extends Event> {

	private String name;
	private Processor<E> top;
	private Processor<E> bottom;
			
	public PNBuilder() {
		this("");
	}

	public PNBuilder(String name) {
		this.name = name;
		
		top = null;
		bottom = null;
	}
	
	public PNBuilder<E> add(Processor<E> processor) {
				
		if (processor.getName().length() == 0) {
			processor.setName(name);
		}
		
		if (top == null) {
			top = processor;
		}
		else {
			bottom.addSuccessor(processor);
		}
		
		bottom = processor;

		return this;
	}
	
	public PNBuilder<E> add(Processor<E> predecessor, Processor<E> processor) {
		bottom = predecessor;
		add(processor);
		return this;
	}
	
	public Processor<E> top() {
		return top;
	}
}

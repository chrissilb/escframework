package de.gwasch.code.escframework.events.utils;


import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.MainPushAction;
import de.gwasch.code.escframework.events.events.PushAction;
import de.gwasch.code.escframework.events.events.SwingPushAction;
import de.gwasch.code.escframework.events.events.TimerAction;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Executor;
import de.gwasch.code.escframework.events.processors.ManagedThread;
import de.gwasch.code.escframework.events.processors.Processor;
import de.gwasch.code.escframework.events.processors.Scheduler;


public class TimerFactory {

	public static <E extends Event> Dispatcher<TimerAction<E>> createTimer(PushAction pushAction) {

		Dispatcher<TimerAction<E>> eventDispatcher;
		
		new PNBuilder<TimerAction<E>>("timer")
			.add(eventDispatcher = new Dispatcher<>())
//			.add(new Initializer<>())
			.add(new Scheduler<>(new PriorityQueue<>(), true))
			.add(new ManagedThread<>(pushAction))
			.add(new Executor<>());	

		return eventDispatcher;
	}
	
	public static <E extends Event> Processor<TimerAction<E>> createSwingTimer() {
		SwingPushAction spa = new SwingPushAction();
		return createTimer(spa);
	}
	
	public static <E extends Event> Processor<TimerAction<E>> createMainTimer(BlockingQueue<Runnable> queue) {
		MainPushAction mpa = new MainPushAction(queue);
		return createTimer(mpa);
	}
}

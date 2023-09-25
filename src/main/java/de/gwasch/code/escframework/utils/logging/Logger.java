package de.gwasch.code.escframework.utils.logging;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

public class Logger {

	public static Filters filters; 
	
	static {
		filters = new Filters();
		filters.add(".*", true);
	}
	
	public static void log(String s) {
		if (filters != null) {
			if (!filters.accept(s)) return;
		}
		
		System.out.println(s);
	}
	
//	public static void log(Event event) {
//		log(event.toString());
//	}
	
//	public static void log(String context, Event event) {
//		log(context + ": " + event);
//	}
	
//	public static void log(String context, Processor<?> processor) {
//		log(context + ": " + processor);
//	}
	
	public static void log(String context, Processor<?> processor, Event event) {
//		log(context + ": " + processor + ": " + threadToString() + ": " + event);
	}
	
	private static String threadToString() {
		return Thread.currentThread().getName();
	}
	
//	public static void log(String context, Processor<?> processor1, Processor<?> processor2, Event event) {
//		log(context + ": " + processor1 + ": " + processor2 + ": " + event);
//	}
	
	
//	public static void log(String context, ProcessListener<?> listener) {
//		log(context + ": " + listener);
//	}
//	
//	public static void log(String context, ProcessListener<?> listener, Event event) {
//		log(context + ": " + listener + ": " + event);
//	}
//	
//	public static void log(String context, ProcessListener<?> listener1, ProcessListener<?> listener2, Event event) {
//		log(context + ": " + listener1 + ": " + listener2 + ": " + event);
//	}
}

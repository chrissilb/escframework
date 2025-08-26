package de.gwasch.code.escframework.utils.logging;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.processors.Processor;

/**
 * A {@code Logger} prints strings to the standard output stream
 * ({@code System.out}) if it is accepted by {@link #filters}.
 */
public class Logger {

	public static Filters filters;

	static {
		filters = new Filters();
		filters.add(".*", true);
	}

	/**
	 * Logs a message if it is accepted by filters.
	 * 
	 * @param message the log message
	 */
	public static void log(String message) {
		if (filters != null) {
			if (!filters.accept(message))
				return;
		}

		System.out.println(message);
	}

	/**
	 * Logs a message if it is accepted by filters.
	 * 
	 * @param context   the context of the message
	 * @param processor the processor
	 * @param event     the event to log
	 */
	public static void log(String context, Processor<?> processor, Event event) {
//		log(context + ": " + processor + ": " + threadToString() + ": " + event);
	}
}

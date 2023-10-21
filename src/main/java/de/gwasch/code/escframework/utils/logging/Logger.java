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

	public static void log(String s) {
		if (filters != null) {
			if (!filters.accept(s))
				return;
		}

		System.out.println(s);
	}

	public static void log(String context, Processor<?> processor, Event event) {
//		log(context + ": " + processor + ": " + threadToString() + ": " + event);
	}
}

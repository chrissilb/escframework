package de.gwasch.code.escframework.events.utils;

import java.text.SimpleDateFormat;

import de.gwasch.code.escframework.events.events.Event;

public class TimeUtil {
	
	public static void setPushTime(Event event, int delayInMillis) {
		long pushTime = System.currentTimeMillis() + delayInMillis;
		event.setPushTime(pushTime);
	}
	
	public static String timestamp() {
		final SimpleDateFormat datefmt = new SimpleDateFormat("HH:mm:ss:SSS");
		return datefmt.format(System.currentTimeMillis());
	}
}

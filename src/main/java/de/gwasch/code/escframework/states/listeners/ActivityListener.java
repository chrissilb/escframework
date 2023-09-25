package de.gwasch.code.escframework.states.listeners;

public interface ActivityListener<T> {
	boolean activity(T newvalue, T oldvalue);
}
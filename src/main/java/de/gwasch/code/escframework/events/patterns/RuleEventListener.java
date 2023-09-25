package de.gwasch.code.escframework.events.patterns;

public interface RuleEventListener {
	void onEvent();
	
	default boolean accept(RuleMode mode) { 
		return false; 
	}
}

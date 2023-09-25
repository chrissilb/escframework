package de.gwasch.code.escframework.events.patterns;

import java.util.Random;

import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.utils.TimeUtil;

public class TriggerIntervalEvent extends AbstractEvent {
	
	private Rule rule;
	
	public TriggerIntervalEvent(Rule rule, Event offsetEvent) {
		this.rule = rule;

		int interval = rule.getTriggerInterval();
		
		if (rule.getMaxTriggerDeviationFactor() != 0) {
			Random random = new Random();		
			double r = random.nextDouble();
			interval = (int)(interval - rule.getMaxTriggerDeviationFactor() * interval
					+ 2 * r * rule.getMaxTriggerDeviationFactor() * interval);
		}
		
		if (offsetEvent != null) {
			setPushTime(offsetEvent.getPushTime() + interval);
		}
		else {
			TimeUtil.setPushTime(this, interval);
		}
		
		
		
	}
	
	public TriggerIntervalEvent(Rule rule) {
		this(rule, null);
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public String toString() {
		return super.toString() + ": " + rule.toString();
	}
}

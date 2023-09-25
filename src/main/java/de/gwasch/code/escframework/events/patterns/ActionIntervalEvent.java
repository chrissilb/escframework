package de.gwasch.code.escframework.events.patterns;

import java.util.Random;

import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.events.utils.TimeUtil;

public class ActionIntervalEvent extends AbstractEvent {
	
	private Rule rule;
	
	public ActionIntervalEvent(Rule rule) {
		this.rule = rule;
		
		int interval = rule.getActionInterval();
		
		if (rule.getMaxActionDeviationFactor() != 0) {
			Random random = new Random();		
			double r = random.nextDouble();
			interval = (int)(interval - rule.getMaxActionDeviationFactor() * interval
					+ 2 * r * rule.getMaxActionDeviationFactor() * interval);
		}		

		if (rule.getActionCount() == 0 && rule.firstActionImmediately()) {
			interval = interval - rule.getActionInterval();
			if (interval < 0) {
				interval = interval * -1;
			}
		}

		if (rule.getLastEvent() != null) {
			setPushTime(rule.getLastEvent().getPushTime() + interval);
		}
		else {
			System.out.println("ActionIntervalEvent: hier");
			TimeUtil.setPushTime(this, interval);
		}
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public String toString() {
		return super.toString() + ": " + rule.toString();
	}
}

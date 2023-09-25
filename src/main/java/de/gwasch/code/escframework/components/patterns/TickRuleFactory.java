package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Tick;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

public class TickRuleFactory implements RuleFactory<Tick> {

	public Rule createRule(Tick tick, Object thiz, Method method) {
		
		InvocationEvent triggerEvent;
		
		return new RuleBuilder().name(Tick.class.getSimpleName().toLowerCase())
		  .triggerPatternEvent(triggerEvent = InstanceAllocator.createInvocationEvent(thiz, method))
		  .actionEvent(triggerEvent)
		  .actionFinishEvent(InstanceAllocator.createReturnEvent(thiz, triggerEvent))
		  .actionInterval(tick.interval())
		  .maxActionDeviationFactor(tick.maxDeviationFactor())
		  .implicitFirstTrigger(true)
		  .maxActionCount(tick.invocations())
		  .firstActionImmediately(true)
		  .activatePatternEvent(InstanceAllocator.createInvocationEvent(thiz, tick.activateMethod()))
		  .deactivatePatternEvent(InstanceAllocator.createInvocationEvent(thiz, tick.deactivateMethod()))
		  .suspendPatternEvent(InstanceAllocator.createInvocationEvent(thiz, tick.suspendMethod()))
		  .resumePatternEvent(InstanceAllocator.createInvocationEvent(thiz, tick.resumeMethod()))
		  .toRule();
	}
}

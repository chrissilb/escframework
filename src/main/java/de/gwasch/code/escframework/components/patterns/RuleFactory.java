package de.gwasch.code.escframework.components.patterns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.events.patterns.Rule;

public interface RuleFactory <T extends Annotation> {
	Rule createRule(T annotation, Object thiz, Method method);
}

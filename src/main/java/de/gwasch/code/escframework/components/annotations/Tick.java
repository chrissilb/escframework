package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.gwasch.code.escframework.components.utils.CodeGenerator;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;

/**
 * Enables cyclic invocations of the annotated method.
 * 
 * @see de.gwasch.code.escframework.events.patterns.Rule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(TickList.class)
@Rule
public @interface Tick {

	/**
	 * Returns the time interval until the next method invocation happens.
	 * 
	 * @return the time interval in milliseconds
	 */
	int interval() default 1000;

	/**
	 * Returns the maximal deviation factor to adjust the interval to a random value
	 * within a certain borders. For example, if interval is set to 1000 and
	 * maxDeviationFactor is set to 0.1 the actual interval is a random number
	 * between 900 and 1100 (exclusive).
	 * 
	 * @return the maximal deviation factor
	 */
	double maxDeviationFactor() default 0.0;

	/**
	 * Returns the number of invocations of the tick method; a negative number means
	 * infinite.
	 * 
	 * @return the number of invocations; a negative number means infinite
	 */
	int invocations() default -1;

	/**
	 * Returns the name of the method which activates ticking. The method <b>must
	 * not</b> be define in the class. Instead it is generated into the component
	 * interface in the following format: {@code void <<<activateMethod>>()}. There
	 * is no implementation of such a method. Instead a corresponding invocation is
	 * consumed by the {@link PatternMatcher}. This code generation is provided by
	 * {@link CodeGenerator}. It can be trigger via the Maven plugin
	 * <a href="https://github.com/chrissilb/escifgen">escifgen</a>.
	 * <a href="https://github.com/chrissilb/demolibrary/blob/main/pom.xml">Here</a>
	 * you can find an example for its usage.
	 * 
	 * @return the activate method name
	 */
	@Generate
	String activateMethod() default "";

	/**
	 * Returns the name of the method which deactivates ticking. The method <b>must
	 * not</b> be define in the class. Instead it is generated into the component
	 * interface in the following format: {@code void <<<deactivateMethod>>()}.
	 * There is no implementation of such a method. Instead a corresponding
	 * invocation is consumed by the {@link PatternMatcher}. This code generation is
	 * provided by {@link CodeGenerator}. It can be trigger via the Maven plugin
	 * <a href="https://github.com/chrissilb/escifgen">escifgen</a>.
	 * <a href="https://github.com/chrissilb/demolibrary/blob/main/pom.xml">Here</a>
	 * you can find an example for its usage.
	 * 
	 * @return the deactivate method name
	 */
	@Generate
	String deactivateMethod() default "";

	/**
	 * Returns the name of the method which suspends ticking. The method <b>must
	 * not</b> be define in the class. Instead it is generated into the component
	 * interface in the following format: {@code void <<<suspendMethod>>()}. There
	 * is no implementation of such a method. Instead a corresponding invocation is
	 * consumed by the {@link PatternMatcher}. This code generation is provided by
	 * {@link CodeGenerator}. It can be trigger via the Maven plugin
	 * <a href="https://github.com/chrissilb/escifgen">escifgen</a>.
	 * <a href="https://github.com/chrissilb/demolibrary/blob/main/pom.xml">Here</a>
	 * you can find an example for its usage.
	 * 
	 * @return the suspend method name
	 */
	@Generate
	String suspendMethod() default "";

	/**
	 * Returns the name of the method which resumes ticking after it was suspended.
	 * The method <b>must not</b> be define in the class. Instead it is generated
	 * into the component interface in the following format:
	 * {@code void <<<resumeMethod>>()}. There is no implementation of such a
	 * method. Instead a corresponding invocation is consumed by the
	 * {@link PatternMatcher}. This code generation is provided by
	 * {@link CodeGenerator}. It can be trigger via the Maven plugin
	 * <a href="https://github.com/chrissilb/escifgen">escifgen</a>.
	 * <a href="https://github.com/chrissilb/demolibrary/blob/main/pom.xml">Here</a>
	 * you can find an example for its usage.
	 * 
	 * @return the resume method name
	 */
	@Generate
	String resumeMethod() default "";
}

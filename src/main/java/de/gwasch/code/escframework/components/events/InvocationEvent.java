package de.gwasch.code.escframework.components.events;

import java.lang.reflect.Proxy;
import java.util.Objects;

import de.gwasch.code.escframework.components.utils.MetaMethod;
import de.gwasch.code.escframework.components.utils.ProxyWrapper;
import de.gwasch.code.escframework.components.utils.Skeleton;
import de.gwasch.code.escframework.components.utils.Stub;
import de.gwasch.code.escframework.events.events.AbstractEvent;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;

/**
 * An {@code InvocationEvent} is used to communicate between {@link Stub}
 * towards {@link Skeleton}. {@link #getSource()} of an InvocationEvent must be
 * a dynamic proxy which is the case for all components. An
 * {@code InvocationEvent} can be handled by {@link PatternMatcher} exclusively
 * if it consumes those events.
 * 
 * @see PatternMatcher
 */
public class InvocationEvent extends AbstractEvent {

	private MetaMethod method;
	private Object[] args;

	/**
	 * Constructs an {@code InvocationEvent}.
	 */
	public InvocationEvent() {
		method = null;
		args = null;
	}

	/**
	 * Constructs an {@code InvocationEvent}.
	 * 
	 * @param source the source of the event which should be a component which
	 *               should be a dynamic proxy
	 * @param method the description of the method to invoke
	 * @param args   the method arguments
	 */
	public InvocationEvent(Object source, MetaMethod method, Object[] args) {
		setSource(source);

		if (method.getMethod() == null) {
			System.out.println("InvocationEvent: keine Method: " + method);
		}

		assert method.getMethod() != null;

		if (!method.getMethod().getDeclaringClass().isInterface()
				&& !Proxy.isProxyClass(method.getMethod().getDeclaringClass())
				&& !method.getMethod().getDeclaringClass().equals(Object.class)) {
			System.out.println("InvocationEvent: keine Interface oder Proxy-Method oder Object-Method: "
					+ method.getMethod().getDeclaringClass());
		}

		this.method = method;
		this.args = args;
	}

	public InvocationEvent clone() {
		InvocationEvent clone = new InvocationEvent(getSource(), method, args);
		return clone;
	}

	public ProxyWrapper getSource() {
		return (ProxyWrapper) super.getSource();
	}

	public void setSource(Object source) {
		super.setSource(new ProxyWrapper((Proxy) source));
	}

	public MetaMethod getMethod() {
		return method;
	}

	public void setMethod(MetaMethod method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof InvocationEvent)) {
			return false;
		}

		InvocationEvent cmp = (InvocationEvent) obj;

		boolean ret = true;
		ret &= getSource().equals(cmp.getSource());
		ret &= method.equals(cmp.method);

		return ret;
	}

	public int hashCode() {
		return Objects.hash(method, getSource());
	}

	public int objectHashCode() {
		return super.hashCode();
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(getClass().getSimpleName());
		sb.append(": ");
		sb.append(getSource());
		sb.append(".");
		sb.append(method);

		return sb.toString();
	}
}

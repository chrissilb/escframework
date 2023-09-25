package de.gwasch.code.escframework.components.events;

import java.lang.reflect.Proxy;
import java.util.Objects;

import de.gwasch.code.escframework.components.utils.MetaMethod;
import de.gwasch.code.escframework.components.utils.ProxyWrapper;
import de.gwasch.code.escframework.events.events.AbstractEvent;

//NOTE: the source of an InvocationEvent must be a dynamic proxy.
public class InvocationEvent extends AbstractEvent {

	private MetaMethod method;
	private Object[] args;
	
	public InvocationEvent() {
		method = null;
		args = null;
	}
	
	public InvocationEvent(Object source, MetaMethod method, Object[] args) {
		setSource(source);
		
		if (method.getMethod() == null) {
			System.out.println("InvocationEvent: keine Method: " + method);
		}
		
		assert method.getMethod() != null;
		
		if (!method.getMethod().getDeclaringClass().isInterface() && !Proxy.isProxyClass(method.getMethod().getDeclaringClass())
				&& !method.getMethod().getDeclaringClass().equals(Object.class)) {
			System.out.println("InvocationEvent: keine Interface oder Proxy-Method oder Object-Method: " + method.getMethod().getDeclaringClass());
		}
		
		this.method = method;
		this.args = args;
	}
	
	public InvocationEvent clone() {
		InvocationEvent clone = new InvocationEvent(getSource(), method, args);
		return clone;
	}
	
	public ProxyWrapper getSource() {
		return (ProxyWrapper)super.getSource();
	}
	
	public void setSource(Object source) {
		super.setSource(new ProxyWrapper((Proxy)source));
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
		if (! (obj instanceof InvocationEvent)) {
			return false;
		}

		InvocationEvent cmp = (InvocationEvent)obj;
		
		boolean ret = true;
		ret &= getSource().equals(cmp.getSource());
		ret &= method.equals(cmp.method);
		
		return ret;
	}
	
	public int hashCode() {
		return Objects.hash(method, getSource());
		//return method.hashCode() ^ getSource().hashCode();
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
		
//		sb.append(" : (");
//			
//		if (args != null && args.length > 0) {
//
//			boolean first = true;
//			for (Object arg : args) {
//				if (first) {
//					first = false;
//				}
//				else {
//					sb.append(", ");
//				}
//				
//				sb.append(arg);
//			}
//		}
//			
//		sb.append(")");
		

		return sb.toString();
	}
}

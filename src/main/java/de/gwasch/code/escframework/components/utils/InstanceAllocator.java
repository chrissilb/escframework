package de.gwasch.code.escframework.components.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.gwasch.code.escframework.components.annotations.After;
import de.gwasch.code.escframework.components.annotations.Asterisk;
import de.gwasch.code.escframework.components.annotations.Base;
import de.gwasch.code.escframework.components.annotations.Client;
import de.gwasch.code.escframework.components.annotations.Core;
import de.gwasch.code.escframework.components.annotations.Delay;
import de.gwasch.code.escframework.components.annotations.Expansion;
import de.gwasch.code.escframework.components.annotations.Extension;
import de.gwasch.code.escframework.components.annotations.Less;
import de.gwasch.code.escframework.components.annotations.More;
import de.gwasch.code.escframework.components.annotations.Service;
import de.gwasch.code.escframework.components.annotations.Thiz;
import de.gwasch.code.escframework.components.annotations.Tick;
import de.gwasch.code.escframework.components.annotations.Within;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.events.ReturnEvent;
import de.gwasch.code.escframework.components.exceptions.InvalidMethodSignatureException;
import de.gwasch.code.escframework.components.exceptions.InvalidRuleAnnotationException;
import de.gwasch.code.escframework.components.exceptions.InvalidTypeException;
import de.gwasch.code.escframework.components.exceptions.MultipleAsteriskInsteadException;
import de.gwasch.code.escframework.components.exceptions.UnknownMethodException;
import de.gwasch.code.escframework.components.exceptions.UnknownTypeException;
import de.gwasch.code.escframework.components.patterns.AfterRuleFactory;
import de.gwasch.code.escframework.components.patterns.DelayRuleFactory;
import de.gwasch.code.escframework.components.patterns.LessRuleFactory;
import de.gwasch.code.escframework.components.patterns.MoreRuleFactory;
import de.gwasch.code.escframework.components.patterns.RuleFactory;
import de.gwasch.code.escframework.components.patterns.TickRuleFactory;
import de.gwasch.code.escframework.components.patterns.WithinRuleFactory;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.events.TimerAction;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Initializer;
import de.gwasch.code.escframework.events.processors.Processor;
import de.gwasch.code.escframework.events.processors.TimerHandler;
import de.gwasch.code.escframework.events.streams.EventInputStream;
import de.gwasch.code.escframework.events.streams.EventOutputStream;
import de.gwasch.code.escframework.events.utils.PNBuilder;
import de.gwasch.code.escframework.events.utils.TimerFactory;
import de.gwasch.code.escframework.utils.gapsort.GapSorter;

/**
 * The {@code InstanceAllocator} is the central interface to an application of the component framework.
 * In particular, it creates services via {@link #create(Class, Object...)}.
 */
public class InstanceAllocator {
	
//	private static class ReturnPredicate implements Predicate<ReturnEvent> {
//
//		public boolean test(ReturnEvent t) {
//			return !t.getInvocationEvent().getMethod().isOneway();
//		}
//	}
	
//	private static final Predicate<ReturnEvent> RETURN_PREDICATE = new ReturnPredicate();

	private static Map<Class<?>, MetaType> types;
	private static Processor<TimerAction<Event>> timerPN;
	private static Processor<Event> invocationPN;
	
	private static EventOutputStream<InvocationEvent> stubOut;
	private static EventInputStream<ReturnEvent> stubIn;
//	private static EventOutputStream<ReturnEvent> skeletonOut;
	private static InvocationPostEventHandler invocationPostEventHandler;
	private static InvocationManager invocationManager;
	private static Dispatcher<Event> dispatcher;
	private static PatternMatcher patternMatcher;
	private static Map<Class<? extends Annotation>, RuleFactory<? extends Annotation>> ruleFactories;

	
	private static Map<Object, Skeleton> proxySkeletonMap;
	
	static {
		types = new HashMap<Class<?>, MetaType>();
		
		timerPN = TimerFactory.createSwingTimer();
		timerPN.activate();
		
		proxySkeletonMap = new HashMap<>();
				
		// create processors
		Dispatcher<Event> patternDispatcher;

		invocationPN = new PNBuilder<Event>("invocation")
			.add(new Initializer<>())
			.add(new TimerHandler<>(timerPN))
			.add(patternDispatcher = new Dispatcher<>("patternDispatcher"))
			.add(dispatcher = new Dispatcher<>())
			.top();			
						
		// configure stub
		stubOut = new EventOutputStream<>(invocationPN);
		stubIn = new EventInputStream<>();
	
		// configure skeleton			
//		skeletonOut = new EventOutputStream<>(invocationPN);
		
		//configure pattern matcher
		patternMatcher = new PatternMatcher(patternDispatcher, invocationPN);
		invocationPostEventHandler = new InvocationPostEventHandler();
		invocationPostEventHandler.setProcessor(invocationPN);
		patternMatcher.setPostEventListener(invocationPostEventHandler);
		invocationManager = new InvocationManager();
		invocationManager.setStreams(stubIn, stubOut);
		patternMatcher.setActionManager(invocationManager);
		dispatcher.register(patternMatcher, ReturnEvent.class, stubIn.getEventHandler());
				
		ruleFactories = new HashMap<>();
		addRuleFactory(After.class, new AfterRuleFactory());
		addRuleFactory(Delay.class, new DelayRuleFactory());
		addRuleFactory(Less.class, new LessRuleFactory());
		addRuleFactory(More.class, new MoreRuleFactory());
		addRuleFactory(Tick.class, new TickRuleFactory());
		addRuleFactory(Within.class, new WithinRuleFactory());
	}
	
	private InstanceAllocator() {
	}
	
	public static Processor<TimerAction<Event>> getTimerPN() {
		return timerPN;
	}
	
	public static Processor<Event> getInvocationPN() {
		return invocationPN;
	}
	
	public static PatternMatcher getPatternMatcher() {
		return patternMatcher;
	}
	
	public static InvocationManager getInvocationManager() {
		return invocationManager;
	}
	
	private static void addType(Class<?> implType) {
		
		MetaType metatype = new MetaType();
		metatype.setImplementationType(implType);
		
		if (implType.isAnnotationPresent(Service.class)) {
			Service a = implType.getAnnotation(Service.class);
			metatype.setInterfaceType(a.type());
			metatype.setBaseType(a.inherits());
			metatype.setInstantiable(a.instantiable());
		}
		else if (implType.isAnnotationPresent(Extension.class)) {
			Extension a = implType.getAnnotation(Extension.class);
			metatype.setInterfaceType(a.type());
			metatype.setBaseType(a.inherits());
			metatype.setExtendingType(a.extendz());
			metatype.setClientType(a.client());
			metatype.setAllowNullClient(a.allowNullClient());
			metatype.setSuccessorExtension(a.before());
			metatype.setPredecessorExtension(a.after());
//			metatype.setAutoDelegate(a.autoDelegate());
		}
		else {
			throw new InvalidTypeException(implType);
		}
		
		types.put(metatype.getInterfaceType(), metatype);
	}
	
	
	public static void collectTypes() {

		ComponentClassLoader cl = new ComponentClassLoader();
		List<Class<?>> classes = cl.getClasses();
		for (Class<?> cls : classes) {
			addType(cls);
		}
		
		resolveTypeDependencies();
	}
	
	//todo, automatisch ausführen, wenn typen "detached"
	private static void resolveTypeDependencies() {
		
		// references...
		for (Class<?> interfaceType : types.keySet()) {
			MetaType metaType = types.get(interfaceType);
			
			Class<?> implType = metaType.getImplementationType();
			
			boolean thizFieldFound = false;
			boolean baseFieldFound = false;
			boolean coreFieldFound = false;
			boolean clientFieldFound = false;

			if (metaType.hasBase()) {
				MetaType base = types.get(metaType.getBaseType());
				metaType.setBase(base);
			}
			
			if (metaType.isExtension()) {
				MetaType core = types.get(metaType.getExtendingType());
				core.addExtension(metaType);
			}

			for (Field field : implType.getDeclaredFields()) {
				
				if (field.isAnnotationPresent(Thiz.class)) {
					
					metaType.setThizField(field);

					Class<?> fieldtype = field.getType();
					
					if (!types.containsKey(fieldtype)) {
						throw new UnknownTypeException(fieldtype);
					}
					
					if (thizFieldFound || !fieldtype.equals(metaType.getInterfaceType())) {
						throw new InvalidTypeException(interfaceType);
					}
					
					thizFieldFound = true;
				}
				
				if (metaType.hasBase() && field.isAnnotationPresent(Base.class)) {
					
					metaType.setBaseField(field);
					
					Class<?> fieldType = field.getType();
					
					if (!types.containsKey(fieldType)) {
						throw new UnknownTypeException(fieldType);
					}
					
					if (baseFieldFound || !fieldType.equals(metaType.getBaseType())) {
						throw new InvalidTypeException(interfaceType);
					}
					
					baseFieldFound = true;
				}
				
				if (metaType.isExtension() && field.isAnnotationPresent(Core.class)) {
				
					metaType.setCoreField(field);
					
					Class<?> fieldType = field.getType();
					
					if (!types.containsKey(fieldType)) {
						throw new UnknownTypeException(fieldType);
					}
					
					if (coreFieldFound || !fieldType.equals(metaType.getExtendingType())) {
						throw new InvalidTypeException(interfaceType);
					}

					coreFieldFound = true;
				}
				
				if (metaType.hasClient() && field.isAnnotationPresent(Client.class)) {
					
					metaType.setClientField(field);
					
					Class<?> fieldType = field.getType();
					
					if (!types.containsKey(fieldType)) {
						throw new UnknownTypeException(fieldType);
					}
					
					if (clientFieldFound || !fieldType.equals(metaType.getClientType())) {
						throw new InvalidTypeException(interfaceType);
					}

					clientFieldFound = true;
				}
				
				if (field.isAnnotationPresent(Expansion.class)) {
					
					metaType.addExpField(field);	
				}
			}

			for (Method method : implType.getDeclaredMethods()) {
				if (method.getAnnotations().length > 0) {

					//NOTE: all annotated methods are candidates for Rule descriptions of the PatternMatcher.
					// Filtering by Rule-annotated Annotations would be inefficient because Repeatable types are not Rule-annotated.
					metaType.addGuardedMethod(method);

					//todo, es darf nur eine instead-method geben und diese muss eine passende signatur haben
					if (method.isAnnotationPresent(Asterisk.class)) {
						Asterisk asterisk = method.getAnnotation(Asterisk.class);
						AsteriskMethod asteriskMethod = new AsteriskMethod(method, asterisk.type());
						metaType.addAsteriskMethod(asteriskMethod);
					}
				}
			}
			
			//NOTE: error handling for asterisk methods
			
			boolean hasInsteadMethod = false;
			
			for (AsteriskMethod asteriskMethod : metaType.getAsteriskMethods()) {
				
				if (asteriskMethod.getAsteriskType() == AsteriskType.INSTEAD_ALL 
					|| asteriskMethod.getAsteriskType() == AsteriskType.INSTEAD_ELSE) {
					
					if (hasInsteadMethod) {
						throw new MultipleAsteriskInsteadException(implType);
					}
					
					if (!isQualifiedAsteriskMethod(asteriskMethod.getMethod(), Object.class)) {
						throw new InvalidMethodSignatureException("'public Object <<methodName>>(Object, Method, Object[])'");		
					}	
					
					hasInsteadMethod = true;
				}
				else {
					if (   !isSimpleAsteriskMethod(asteriskMethod.getMethod())
						&& !isQualifiedAsteriskMethod(asteriskMethod.getMethod(), void.class)) {
						throw new InvalidMethodSignatureException( 
								"'public void <<<methodName>>()' or 'public void <<methodName>>(Object, Method, Object[])'");
					}
				}
			}
		}
		
		// inheritance...
//		for (Class<?> interfaceType : types.keySet()) {
//			MetaType metaType = types.get(interfaceType);
//			if (metaType.isDissolved()) continue;
//			inherit(metaType);
//		}
		
		// before, after...
		for (Class<?> interfacetype : types.keySet()) {
			MetaType metatype = types.get(interfacetype);
			
			if (metatype.isExtension()) continue;
			
			GapSorter.sort(metatype.getExtensions());
			
			if (!isSorted(metatype.getExtensions())) {
				
				for (MetaType ext : metatype.getExtensions()) {
					System.out.println(ext.getInterfaceType());
				}
				
				throw new InvalidTypeException(interfacetype);
			}
		}
		
//		for (MetaType metaType : types.values()) {
//			
//			if (metaType.getBase() != null) {
//				MetaType base = metaType.getBase();
//				
//				if (base.getExtensions().size() > 0) {
//					metaType.setActualExtending(base.getExtensions().get(0));
//				}
//				else {
//					metaType.setActualBase(base);
//				}
//			}
//
//			if (metaType.getExtendingType() != null) {
//				MetaType extending = types.get(metaType.getExtendingType());
//				
//				if (extending.getExtensions().size() > 0) {
//					metaType.setActualExtending(extending.getExtensions().get(0));
//				}
//				else {
//					metaType.setActualExtending(extending);
//				}
//			}
//		}
	}
	
	private static boolean isSimpleAsteriskMethod(Method method) {
		boolean ret =  method.getReturnType() == void.class
					&& (method.getModifiers() & Modifier.PUBLIC) != 0
					&& method.getParameterCount() == 0;
			
			return ret;
	}

	private static boolean isQualifiedAsteriskMethod(Method method, Class<?> expectedReturnType) {
		
		boolean ret =  method.getReturnType() == expectedReturnType
					&& (method.getModifiers() & Modifier.PUBLIC) != 0
					&& method.getParameterCount() == 3
					&& method.getParameterTypes()[0] == Object.class
					&& method.getParameterTypes()[1] == Method.class
					&& method.getParameterTypes()[2].isArray()
					&& method.getParameterTypes()[2].getComponentType() == Object.class;
		
		return ret;
	}

//	private static void inherit(MetaType metaType) {
//		
//		if (metaType.isDissolved()) {
//			return;
//		}
//		
//		Class<?> baseInterface = metaType.getBaseType();
//		
//		if (baseInterface == null) {
//			metaType.setDissolved(true);
//			return;
//		}
//		
//		MetaType base = types.get(baseInterface);
//		inherit(base);
//		
//		MetaType pseudoBase = base.clone();
////		pseudoBase.setIsBase(true);
//		metaType.setBase(pseudoBase);
//		
//		for (MetaType extbase : base.getExtensions()) {
//			inherit(extbase);
//			
//			if (!metaType.isInheritingExtension(extbase)) {
//				MetaType pseudoExt = extbase.clone();
//				pseudoExt.setExtendingType(metaType.getInterfaceType());
//				metaType.addExtension(pseudoExt);
//			}
//		}
//		
//		for (int i = 0; i < base.getExpansions().size(); i++) {
//			
//			MetaType expBase = base.getExpansions().get(i);
//			Field expField = base.getExpFields().get(i);
//			Class<?> expType = base.getExpTypes().get(i);
//			
//			inherit(expBase);
//			
//			metaType.addExpansion(expBase);
//			metaType.addExpField(expField);
//			metaType.addExpType(expType);
//		}
//		
//		if (metaType.getPredecessor() == null) {
//			metaType.setPredecessor(base.getPredecessor());
//		}
//		
//		if (metaType.getSuccessor() == null) {
//			metaType.setSuccessor(base.getSuccessor());
//		}
//	}

	private static boolean isSorted(List<MetaType> list) {
		
		for (int i = 0; i < list.size(); i++) {
			
			MetaType metatype = list.get(i);
			
			if (metatype.getSuccessorExtension() != null) {
				int index = list.indexOf(types.get(metatype.getSuccessorExtension()));
				if (index == -1 || index >= i) {
					return false;
				}
			}
			
			if (metatype.getPredecessorExtension() != null) {
				int index = list.indexOf(types.get(metatype.getPredecessorExtension()));
				if (index == -1 || index <= i) {
					return false;
				}
			}
		}
		
		return true;
	}
			
	public static InvocationEvent createInvocationEvent(Object thiz, String methodName) {
		return createInvocationEvent(thiz, methodName, new Class<?>[0], void.class);
	}
	
	public static InvocationEvent createInvocationEvent(Object thiz, String methodName, Class<?>[] paramTypes, Class<?> returnType) {
		
		if (methodName == null || methodName.length() == 0) {
			return null;
		}
		
		Method method = ProxyWrapper.getMethod(thiz, methodName, paramTypes);
				
		if (method == null) {
			throw new UnknownMethodException("public " + returnType + " " + methodName + "(" + Arrays.toString(paramTypes) + ")");
		}

		if (!method.getReturnType().equals(returnType)) {
			throw new InvalidMethodSignatureException("public " + returnType + " " + methodName + "(" + Arrays.toString(paramTypes) + ")");
		}

		MetaMethod metaMethod = new MetaMethod(method);
		InvocationEvent invocationEvent = createInvocationEvent(thiz, metaMethod);
		
		return invocationEvent;
	}
	
	public static InvocationEvent createInvocationEvent(Object thiz, Method method) {
		return createInvocationEvent(thiz, method.getName(), method.getParameterTypes(), method.getReturnType());
	}
	
	public static InvocationEvent createInvocationEvent(Object thiz, MetaMethod metaMethod) {
				
		InvocationEvent invocationEvent = new InvocationEvent(thiz, metaMethod, null);
		return invocationEvent;
	}	
	
	public static ReturnEvent createReturnEvent(Object thiz, InvocationEvent invocationEvent) {
		
		Skeleton skeleton = proxySkeletonMap.get(new ProxyWrapper((Proxy)thiz));
		
		ReturnEvent returnEvent = new ReturnEvent(skeleton, invocationEvent);
		return returnEvent;
	}
	
	private static List<Skeleton> skeletons;
	
	//todo, aussagekräftigere exceptions nutzen, insb. auch invalidtype
	/**
	 * Creates a {@code Service}
	 * <p>
	 * The {@code Service} instance is represented as a dynamic proxy. Involved extensions are included. Attached rules
	 * are created and registered at the {@code PatternMatcher}.
	 * 
	 * @param <T> service interface type
	 * @param interfaceType {@code Class} of the service interface type. It must reflect an instantiable service interface. 
	 * @param args constructor arguments. If avoidable {@code args} shall not be used. Instead services should provide
	 *        default constructors, only. Otherwise corresponding parameterization is not type-safe.
	 *        
	 * @return the service instance
	 * 
	 * @see Proxy
	 * @see PatternMatcher
	 */
	public static<T> T create(Class<T> interfaceType, Object... args) {

		MetaType metaType = (MetaType)types.get(interfaceType);
		
		if (metaType.isExtension() || !metaType.isInstantiable()) {
			throw new UnknownTypeException(interfaceType);
		}

		try {
			skeletons = new ArrayList<>();
			
			T proxy = doCreate(interfaceType, metaType, null, args);
			
			Stub stub = (Stub)Proxy.getInvocationHandler(proxy);
			stub.setServiceEntrance(true);
			
			setThiz(proxy);
			
			List<Method> guardedMethods = metaType.getAllGuardedMethods();
			List<Rule> thizRules = createRules(guardedMethods, proxy);
			
			for (Rule rule : thizRules) {
				if (rule.getPatternEventControl("activate").getPatternEvent() == null) {
					rule.getPatternEventControl("activate").getRuleEventListener().onEvent();
				}
			}
			
			return proxy;
		}
		catch (Exception e) {
			throw new UnknownTypeException(metaType.getInterfaceType(), e);
		} 
	}

	
	private static<T> T doCreate(Class<T> interfaceType, MetaType metaType, Object core, Object... args) {

		try {	
//			Object service = metatype.getImplementationType().getDeclaredConstructor().newInstance();
			Object service = metaType.getImplementationType().getDeclaredConstructors()[0].newInstance(args);

			Stub stub = new Stub(metaType); 
			
			ClassLoader clsLoader = ClassLoader.getSystemClassLoader(); 
			
			
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			interfaces.add(metaType.getInterfaceType());
			
			Object base = null;
			
			if (metaType.hasBase()) {
				base = doCreate(metaType.getBase().getInterfaceType(), metaType.getBase(), null);
				
				//todo, der basetype selbst müsste nicht hinzugefügt werden, weil das interface schon davon erbt
				interfaces.addAll(Arrays.asList(base.getClass().getInterfaces()));

				
				if ( metaType.getBaseField() != null) {
					Field f = metaType.getBaseField();
					f.setAccessible(true);
					f.set(service, base);
					f.setAccessible(false);
				}
			}
			
			if (core != null) {
				interfaces.addAll(Arrays.asList(core.getClass().getInterfaces()));
			}
						
			Object proxy = Proxy.newProxyInstance(clsLoader, interfaces.toArray(new Class<?>[0]), stub); 

			
			if (core != null && metaType.getCoreField() != null) {
				Field f = metaType.getCoreField();
				f.setAccessible(true);
				f.set(service, core);
				f.setAccessible(false);
			}
			
			Object extCore = proxy;

			for (MetaType extensionType : metaType.getExtensions()) {
				Object extension = doCreate(extensionType.getInterfaceType(), extensionType, extCore);
				extCore = extension;
			}
			
			Object root = extCore;
		
		
			Skeleton skeleton = new Skeleton(metaType, service, base, core);
			skeletons.add(skeleton);
			
			// install streams
			dispatcher.register(skeleton, ReturnEvent.class, stubIn.getEventHandler());
//			dispatcher.registerListenerPredicate(stubIn.getEventHandler(), RETURN_PREDICATE);
//			stub.setStreams(stubIn, stubOut);
			dispatcher.register(new ProxyWrapper((Proxy)proxy), InvocationEvent.class, skeleton.getEventHandler());
//			skeleton.setOut(skeletonOut);
			skeleton.setPostEventListener(invocationPostEventHandler);
			
			proxySkeletonMap.put(new ProxyWrapper((Proxy)proxy), skeleton);	//todo, eigentlich werden nur die thiz-proxys benötigt
			
			T obj = interfaceType.cast(root);
						
			return obj; 
		}
		catch (Exception e) {
			throw new UnknownTypeException(metaType.getInterfaceType(), e);
		} 
	}
	
	private static void setThiz(Object thiz) throws Exception {
	
		for (Skeleton skeleton : skeletons) {
			MetaType metaType = skeleton.getMetaType();
			Object service = skeleton.getService();
		
			if (metaType.getThizField() != null) {
				Field f = metaType.getThizField();
				f.setAccessible(true);
				f.set(service, thiz);
				f.setAccessible(false);
			}
		}
	}
	
	
	private static <T extends Annotation> void addRuleFactory(Class<T> ruleAnnotationType, RuleFactory<T> ruleFactory) {
				
		if (!ruleAnnotationType.isAnnotationPresent(de.gwasch.code.escframework.components.annotations.Rule.class)) {
			throw new InvalidRuleAnnotationException(ruleAnnotationType);
		}
		
		ruleFactories.put(ruleAnnotationType, ruleFactory);
	}
	
	public static <T extends Annotation> void removeRuleFactory(Class<T> ruleAnnotationType) {
		ruleFactories.remove(ruleAnnotationType);
	}
		
	private static <T extends Annotation> Rule createRule(T annotation, Object thiz, Method method) {

		@SuppressWarnings("unchecked")
		RuleFactory<T> ruleFactory = (RuleFactory<T>)ruleFactories.get(annotation.annotationType());
		
		if (ruleFactory != null) {
			return ruleFactory.createRule(annotation, thiz, method);
		}
		else {
			return null;
		}
	}
	
	private static List<Rule> createRules(List<Method> guardedMethods, Object thiz) throws IllegalAccessException, InvocationTargetException {
			
		List<Rule> thizRules = new ArrayList<>();
							
		for (Method method : guardedMethods) {
							
			Annotation[] annotations = method.getAnnotations();
							
			for (Annotation annotation : annotations) {

				if (ruleFactories.containsKey(annotation.annotationType())) {
					Rule rule = createRule(annotation, thiz, method);
					if (rule != null) {
						patternMatcher.addRule(rule);
						thizRules.add(rule);
					}
					continue;
				}
				
				// NOTE: try to dissolve repeatable annotation:
				
				Class<? extends Annotation> annotationType = annotation.annotationType();
				Method[] listMethods = annotationType.getDeclaredMethods();
				
				if (listMethods.length != 1) {
					continue;
				}
				
				Method valueMethod = listMethods[0];
				
				if (!valueMethod.getName().equals("value")) {
					continue;
				}
				
				Class<?> elementType = valueMethod.getReturnType().componentType();
				
				if (!ruleFactories.containsKey(elementType)) {
					continue;
				}

				if (!elementType.isAnnotationPresent(Repeatable.class)) {
					continue;
				}
				
				Repeatable repeatable = elementType.getAnnotation(Repeatable.class);
				
				if (!repeatable.value().equals(annotationType)) {
					continue;
				}
				
				Annotation[] array = (Annotation[])valueMethod.invoke(annotation);
					
				for (Annotation a : array) {
					Rule rule = createRule(a, thiz, method);
					patternMatcher.addRule(rule);
					thizRules.add(rule);
				}
			}
		}
		
		return thizRules;
	}
}

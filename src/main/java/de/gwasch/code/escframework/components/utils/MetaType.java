package de.gwasch.code.escframework.components.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.gwasch.code.escframework.utils.gapsort.GapComparable;

public class MetaType implements GapComparable<MetaType> {
	
	//todo, Class<?> instanzen aufbereiten, um multi-polymorphie zu unterstützen
	private Class<?> interfaceType;
	private Class<?> implementationType;
	
	private boolean instantiable;
			
	private Field thizField;
	
	private Class<?> baseType;		// todo, redundant, weil entspricht base.getInterfaceType()?
	private Field baseField;
	
	private Class<?> extendingType;
	private Field coreField;
	
	private Class<?> clientType;
	private boolean allowNullClient;
	private Field clientField;
	
	private List<Field> expFields;
	
	private List<Method> guardedMethods;
	private List<AsteriskMethod> asteriskMethods;
	
	private Class<?> successorExtension;
	private Class<?> predecessorExtension;

	private MetaType base;
	private List<MetaType> extensions;

//	private MetaType actualBase;
//	private MetaType actualExtending;
	
//	private boolean autoDelegate;

//	private boolean isBase;
//	private boolean resolved;
	
	
	public MetaType() {
		
		interfaceType = null;
		implementationType = null;
		
		instantiable = false;
		
		base = null;
		extensions = new ArrayList<>();
		
		thizField = null;
		
		baseType = null;
		baseField = null;
		
		extendingType = null;
		coreField= null;
		
		clientType = null;
		allowNullClient = false;
		clientField = null;
		
		expFields = new ArrayList<>();
		
		guardedMethods = new ArrayList<>();
		asteriskMethods = new ArrayList<>();
		
		successorExtension = null;
		predecessorExtension = null;
		
//		actualBase = null;
//		actualExtending = null;
		
//		isBase = false;
//		dissolved = false;
	}
	
//	public MetaType clone() {
//		MetaType copy = new MetaType();
//		copy.interfaceType = interfaceType;
//		copy.implementationType = implementationType; 
//		copy.instantiable = instantiable;
//		copy.base = base;
//		copy.extensions = extensions;
////		copy.autoDelegate = autoDelegate;
//		copy.thizField = thizField;
//		copy.baseType = baseType;
//		copy.baseField = baseField;
//		copy.extendingType = extendingType;
//		copy.coreField = coreField;
//		copy.clientType = clientType;
//		copy.clientField = clientField;
//		copy.expFields = expFields;
//		copy.successor = successor;
//		copy.predecessor = predecessor;
////		copy.isBase = isBase;
////		copy.dissolved = dissolved;
//		return copy;
//	}
	
	public Class<?> getInterfaceType() {
		return interfaceType;
	}
	
	public void setInterfaceType(Class<?> interfacetype) {
		interfaceType = interfacetype;
	}
	
	public Class<?> getImplementationType() {
		return implementationType;
	}
	
	public void setImplementationType(Class<?> impltype) {
		implementationType = impltype;
	}
	
	public boolean isInstantiable() {
		return instantiable;
	}
	
	public void setInstantiable(boolean instantiable) {
		this.instantiable = instantiable;
	}
	
	public Field getThizField() {
		return thizField;
	}
	
	public void setThizField(Field base) {
		thizField = base;
	}
	
	public Class<?> getBaseType() {
		return baseType;
	}
	
	public boolean hasBase() {
		return baseType != null;
	}
	
	public void setBaseType(Class<?> type) {
		if (type == null || type.equals(void.class)) return;
		baseType = type;
	}
	
	public Field getBaseField() {
		return baseField;
	}
	
	public void setBaseField(Field base) {
		baseField = base;
	}
	
	public Class<?> getExtendingType() {
		return extendingType;
	}
	
	public boolean isExtension() {
		return extendingType != null;
	}

	public void setExtendingType(Class<?> type) {
		if (type == null || type.equals(void.class)) return;
		extendingType = type;
	}
	
	public Field getCoreField() {
		return coreField;
	}
	
	public void setCoreField(Field base) {
		coreField = base;
	}
	
	public Class<?> getClientType() {
		return clientType;
	}
	
	public boolean hasClient() {
		return clientType != null;
	}

	
	public void setClientType(Class<?> type) {
		if (type == null || type.equals(void.class)) return;
		clientType = type;
	}
	
	public boolean allowNullClient() {
		return allowNullClient;
	}
	
	public void setAllowNullClient(boolean allowNullClient) {
		this.allowNullClient = allowNullClient;
	}
	
	public Field getClientField() {
		return clientField;
	}
	
	public void setClientField(Field base) {
		clientField = base;
	}
	
	public void addExpField(Field expfield) {
		expFields.add(expfield);
	}

	public List<Field> getExpFields() {
		return expFields;
	}
	
	public boolean hasExpansions() {
		return expFields.size() > 0;
	}

	public void addGuardedMethod(Method guardedMethod) {
		guardedMethods.add(guardedMethod);
	}

	public List<Method> getAllGuardedMethods() {
		
		List<Method> guardedMethods = new LinkedList<>();
		guardedMethods.addAll(this.guardedMethods);
		
		if (base != null) {
			guardedMethods.addAll(base.getAllGuardedMethods());
		}
		
		for (MetaType extension : extensions) {
			guardedMethods.addAll(extension.getAllGuardedMethods());
		}

		//NOTE: expansions are not considered because they do not belong to "this" Object
		
		return guardedMethods;
	}
	
	public List<Method> getGuardedMethods() {
		return guardedMethods;
	}

	public void addAsteriskMethod(AsteriskMethod asteriskMethod) {
		asteriskMethods.add(asteriskMethod);
	}
	
	public List<AsteriskMethod> getAsteriskMethods() {
		return asteriskMethods;
	}
	
	public Class<?> getSuccessorExtension() {
		return successorExtension;
	}
	
	public void setSuccessorExtension(Class<?> type) {
		if (type == null || type.equals(void.class)) return;
		successorExtension = type;
	}
	
	public Class<?> getPredecessorExtension() {
		return predecessorExtension;
	}
	
	public void setPredecessorExtension(Class<?> type) {
		if (type == null || type.equals(void.class)) return;
		predecessorExtension = type;
	}

	public MetaType getBase() {
		return base;
	}
	
	public void setBase(MetaType base) {
		this.base = base;
	}

	public void addExtension(MetaType extension) {
		extensions.add(extension);
	}

	public List<MetaType> getExtensions() {
		return extensions;
	}
	
//	public MetaType getActualBase() {
//		return actualBase;
//	}
//	
//	public void setActualBase(MetaType actualBase) {
//		this.actualBase = actualBase;
//	}
//	
//	public MetaType getActualExtending() {
//		return actualExtending;
//	}
//	
//	public void setActualExtending(MetaType actualExtending) {
//		this.actualExtending = actualExtending;
//	}
	
	
//	public boolean isAutoDelegate() {
//		return autoDelegate;
//	}
//	
//	public void setAutoDelegate(boolean autodelegate) {
//		autoDelegate = autodelegate;
//	}
	
//	public boolean isBase() {
//		return isBase;
//	}
//	
//	public void setIsBase(boolean isBase) {
//		this.isBase = isBase;
//	}

	//todo, löschen
//	public boolean isDissolved() {
//		return dissolved;
//	}
//	
//	public void setDissolved(boolean dissolved) {
//		this.dissolved = dissolved;
//	}
	
//	public boolean isInheriting(MetaMethod metaMethod, Object[] args) {
//		
//		if (base == null) return false;
//		
//		Class<?> cls = base.getInterfaceType();
//		
//		Method method = metaMethod.getDeclaredMethod(cls, args);
//		if (method != null) return true;
//		
//		return base.isInheriting(metaMethod, args);
//	}
	
	public boolean isInheriting(MetaMethod metaMethod, Object[] args) {
		
		if (hasBase()) {
			return base.hasMethod(metaMethod, args);
		}
		else {
			return false;
		}
	}
	
	public boolean hasMethod(MetaMethod metaMethod, Object[] args) {
		
		Method method = metaMethod.getDeclaredMethod(interfaceType, args);
		
		if (method != null) {
			return true;
		}
		
		
		for(MetaType extension : extensions) {
			if (extension.hasMethod(metaMethod, args)) {
				return true;
			}
		}
		
		if (hasBase()) {
			if (base.hasMethod(metaMethod, args)) {
				return true;
			}
		}
		
		return false;
	}
		
	public boolean isInheriting(MetaType base) {
		if (this.base == null) return false;
		if (this.base.equals(base)) return true;
		return this.base.isInheriting(base);
	}
	
	public boolean isInheritingExtension(MetaType base) {
		
		for (MetaType extension : extensions) {
			
			if (extension.isInheriting(base)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean equals(Object obj) {
		MetaType cmp = (MetaType)obj;
		return getInterfaceType().equals(cmp.getInterfaceType());
	}

	public Result compareTo(MetaType cmp) {
		if (successorExtension != null && successorExtension.equals(cmp.interfaceType)
			|| cmp.predecessorExtension != null && cmp.predecessorExtension.equals(interfaceType)) {
			//System.out.println(interfaceType.getSimpleName() + " > " + cmp.interfaceType.getSimpleName());
			return Result.Greater;
		}
		
		if (cmp.successorExtension != null && cmp.successorExtension.equals(interfaceType)
			|| predecessorExtension != null && predecessorExtension.equals(cmp.interfaceType)) {
			//System.out.println(interfaceType.getSimpleName() + " < " + cmp.interfaceType.getSimpleName());
			return Result.Lower;
		}
		
		//System.out.println(interfaceType.getSimpleName() + " == " + cmp.interfaceType.getSimpleName());
		
		return Result.Unknown;
	}
	
	public String toString() {
		return interfaceType.getName();
	}
}
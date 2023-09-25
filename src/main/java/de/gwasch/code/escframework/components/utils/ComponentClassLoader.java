package de.gwasch.code.escframework.components.utils;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import de.gwasch.code.escframework.components.annotations.Extension;
import de.gwasch.code.escframework.components.annotations.Service;
import de.gwasch.code.escframework.components.exceptions.GenerationException;

public class ComponentClassLoader {
	
	private ClassLoader contextClassLoader;
	private String basePackageName;

	public ComponentClassLoader(ClassLoader contextClassLoader, String basePackageName) {
		this.contextClassLoader = contextClassLoader;
		this.basePackageName = basePackageName;
	}
	
	public ComponentClassLoader() {
		this(ClassLoader.getSystemClassLoader(), "");
	}

	public ComponentClassLoader(ClassLoader contextClassLoader) {
		this(contextClassLoader, "");
	}

	public ComponentClassLoader(String basePackageName) {
		this(ClassLoader.getSystemClassLoader(), basePackageName);
	}

	public List<Class<?>> getClasses() {

		try {
			String path = basePackageName.replace('.', '/');
	
			
			Enumeration<URL> resources = contextClassLoader.getResources(path);
	
			List<File> dirs = new ArrayList<File>();
	
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
	
			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, basePackageName));
			}
	
			return classes;
		}
		catch(Exception e) {
			throw new GenerationException(e);
		}
	}

	private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		if (!directory.exists()) {
			return classes;
		}
		
		if (packageName.length() > 0) {
			packageName = packageName + '.';
		}

		File[] files = directory.listFiles();

		for (File file : files) {

			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + file.getName()));
			} 
			else if (file.getName().endsWith(".class")) {
				String clsname = packageName + file.getName().substring(0, file.getName().length() - 6);
				
				Class<?> cls = contextClassLoader.loadClass(clsname);
				
				if (cls.isAnnotationPresent(Service.class) || cls.isAnnotationPresent(Extension.class)) {
					classes.add(cls);
				}
			}
		}

		return classes;
	}
}
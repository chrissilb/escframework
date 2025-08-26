package de.gwasch.code.escframework.components.utils;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import de.gwasch.code.escframework.components.annotations.Asterisk;
import de.gwasch.code.escframework.components.annotations.Expansion;
import de.gwasch.code.escframework.components.annotations.Extension;
import de.gwasch.code.escframework.components.annotations.Generate;
import de.gwasch.code.escframework.components.annotations.PatternControlMethod;
import de.gwasch.code.escframework.components.annotations.Rule;
import de.gwasch.code.escframework.components.annotations.Service;
import de.gwasch.code.escframework.components.exceptions.GenerationException;
import de.gwasch.code.escframework.components.exceptions.InvalidTypeException;	

/**
 * Generates interfaces of components. The interfaces contain all public methods of classes annotated by {@link Service} or {@link Extension}.
 * <p>
 * Furthermore:  
 * <ul>
 * <li>{@code get<<interface name>>Impl()} methods are generated if classes natively explicitly extend a class. 
 * This is needed to get to get access to natively inherited methods.</li>
 * </ul>
 * <p>
 * Precondition for a working code generation:
 * <ul>
 * <li>{@code import} statements are complete</li>
 * <li>no "*" used in {@code import} statements</li>
 * </ul>
 */
public class CodeGenerator {
	
	private static class GenType {
		private StringBuffer sbHeader;
		private StringBuffer sbDecl;
		private StringBuffer sbBody;
		private boolean instantiable; // NOTE: only relevant for Service types
		
		public GenType() {
			this.sbHeader = new StringBuffer();
			this.sbDecl = new StringBuffer();
			this.sbBody = new StringBuffer();
			this.instantiable = serviceInstantiableDefault;
		}
		
		public StringBuffer getSbHeader() {
			return sbHeader;
		}
		
		public StringBuffer getSbDecl() {
			return sbDecl;
		}
		
		public StringBuffer getSbBody() {
			return sbBody;
		}
		
		public boolean isInstantiable() {
			return instantiable;
		}
		
		public void setInstantiable(boolean instantiable) {
			this.instantiable = instantiable;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(sbHeader);
			sb.append(sbDecl);
			sb.append('{');
			sb.append(sbBody);
			sb.append('}');
			return sb.toString();
		}
	}
	
	static boolean serviceInstantiableDefault;
	
	static {
		try {
			Class<Service> cls = Service.class;
			Method m = cls.getDeclaredMethod("instantiable");
			serviceInstantiableDefault = (boolean)m.getDefaultValue();
		} 
		catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String basePackageName;
	private Path targetPath;
	private Map<String, Class<?>> systemRuleAnnotations;
	private Map<String, AnnotationDeclaration> appRuleAnnotations;

	private SourceRoot sourceRoot;
	private SourceRoot targetRoot;
	
	private Map<String, GenType> genTypes;
	
	private Map<String, ImportDeclaration> implImports;
	private Set<ImportDeclaration> interfaceImports;
	private String implPackageName;
	private String packageName;
	private List<String> baseInterfaces;
	
	private Set<String> expansionTypes;
	private Map<String, Set<String>> serviceSubtypes;
	

	/**
	 * Creates a {@code CodeGenerator}.
	 * @param sourcePath the source path to find components. In Maven project e.g. "src/main/java"
	 * @param basePackageName the base package name to restrict searching for components
	 * @param targetPath the path for generated files. In Maven projects e.g. "target/generated-sources"
	 */
	public CodeGenerator(Path sourcePath, String basePackageName, Path targetPath) {

		if (basePackageName == null || basePackageName.length() == 0) {
			throw new GenerationException("basePackageName is undefined.");
		}
		this.basePackageName = basePackageName;
		this.targetPath = targetPath;
		
		systemRuleAnnotations = new HashMap<>();
		loadFrameworkRuleAnnotations();
		appRuleAnnotations = new HashMap<>();
				
		sourceRoot = new SourceRoot(sourcePath);

		try {
			Files.createDirectories(targetPath);
		}
		catch(IOException e) {
			throw new GenerationException(e);
		}
		targetRoot = new SourceRoot(targetPath);
	}

	/**
	 * Creates a {@code CodeGenerator}.
	 * @param sourceFolder the source path to find components. In Maven project e.g. "src/main/java"
	 * @param basePackageName the base package name to restrict searching for components
	 * @param targetFolder the path for generated files. In Maven projects e.g. "target/generated-sources"
	 */
	public CodeGenerator(String sourceFolder, String basePackageName, String targetFolder) {
		this(Path.of(sourceFolder), basePackageName, Path.of(targetFolder));
	}
	
	protected String getInterfaceName(String implTypeName) {
		if (!implTypeName.endsWith("Impl")) {
			throw new InvalidTypeException(implTypeName);
		}
		
		int offset = basePackageName.length() + 1;
		String corename = implTypeName.substring(offset, implTypeName.length() - 4);
		
		String itypename = basePackageName + ".interfaces." + corename;
		return itypename;
	}
	
	
	private void loadFrameworkRuleAnnotations() {
		
		Reflections reflections = new Reflections(Rule.class.getPackageName());
		Collection<Class<?>> ruleClasses = reflections.get(TypesAnnotated.with(Rule.class).asClass());

		for (Class<?> ruleClass : ruleClasses) {
			systemRuleAnnotations.put(ruleClass.getName(), ruleClass);
		}
	}
		
	private static String getPackageName(String name) {
		String packageName = name.substring(0, name.lastIndexOf('.'));
		return packageName;
	}
	
	private static String getPackageName(TypeDeclaration<?> typeDeclaration) {
		
		CompilationUnit cu = (CompilationUnit)typeDeclaration.getParentNode().get();
		Optional<PackageDeclaration> opd = cu.getPackageDeclaration();
		
		String packageName;
		
		if (opd.isPresent()) {
			packageName = cu.getPackageDeclaration().get().getNameAsString() + '.';
		}
		else {
			packageName = "";
		}
		
		return packageName;
	}
	
	private static String getShortName(String name) {
		String shortName = name.substring(name.lastIndexOf('.') + 1);
		return shortName;
	}
	
	//todo, Klassen von java.lang fehlen
	private String getFullName(String shortName) {
		
		if (shortName.contains(".")) {
			return shortName;
		}
		
		ImportDeclaration id = implImports.get(shortName);
		
		if (id != null) {
			return id.getNameAsString();
		}
		else {
			return implPackageName + shortName;
		}
	}
		
	private static boolean hasAnnotationAttr(AnnotationExpr annotationExpr, String attributeName) {
				
		for (MemberValuePair mvp : annotationExpr.findAll(MemberValuePair.class)) {
			if(!mvp.getNameAsString().equals(attributeName)) {
				continue;
			}
			
			return true;
		}

		return false;
	}
	
	private static boolean hasAnnotationAttr(NodeWithAnnotations<?> impltype, Class<? extends Annotation> annotationType, String attributeName) {
		
		if (!impltype.isAnnotationPresent(annotationType)) {
			return false;
		}
				
		AnnotationExpr ae = impltype.getAnnotationByClass(annotationType).get(); 
		
		return hasAnnotationAttr(ae, attributeName);
	}
	
	private static AnnotationExpr getAnnotationExpr(NodeWithAnnotations<?> impltype, Class<? extends Annotation> annotationType) {
		AnnotationExpr ae = impltype.getAnnotationByClass(annotationType).get(); 
		return ae;
	}
	
	private static Expression getAnnotationAttrValue(NodeWithAnnotations<?> implType, Class<? extends Annotation> annotationType, String attributeName) {
		
		AnnotationExpr ae = getAnnotationExpr(implType, annotationType);
		return getAnnotationAttrValue(ae, attributeName);
	}
	
	private static Expression getAnnotationAttrValue(AnnotationExpr annotationExpr, String attributeName) {
				
		for (MemberValuePair mvp : annotationExpr.findAll(MemberValuePair.class)) {
			if(!mvp.getNameAsString().equals(attributeName)) {
				continue;
			}
						
			return mvp.getValue();
		}

		return null;
	}
		
	private static String getAnnotationAttrValueAsTypeName(NodeWithAnnotations<?> implType, Class<? extends Annotation> annotationType, String attributeName) {
		
		Expression expr = getAnnotationAttrValue(implType, annotationType, attributeName);
		if (expr == null) return null;
						
		ClassExpr v = (ClassExpr)expr;
		Type t = v.getType();
		return t.asString();
	}
	
	private static Boolean getAnnotationAttrValueAsBoolean(NodeWithAnnotations<?> implType, Class<? extends Annotation> annotationType, String attributeName) {
		
		Expression expr = getAnnotationAttrValue(implType, annotationType, attributeName);
		if (expr == null) return null;
						
		BooleanLiteralExpr v = (BooleanLiteralExpr)expr;
		return v.getValue();
	}
	
//	private static String getAnnotationAttrValueAsString(NodeWithAnnotations<?> implType, Class<? extends Annotation> annotationType, String attributeName) {
//		
//		Expression expr = getAnnotationAttrValue(implType, annotationType, attributeName);
//		if (expr == null) return null;
//						
//		StringLiteralExpr v = (StringLiteralExpr)expr;
//		return v.getValue();
//	}
	
	private static String getAnnotationAttrValueAsString(AnnotationExpr annotationExpr, String attributeName) {
		
		Expression expr = getAnnotationAttrValue(annotationExpr, attributeName);
		if (expr == null) return null;
						
		StringLiteralExpr v = (StringLiteralExpr)expr;
		return v.getValue();
	}

	
	private void deriveImports(String name) {
		
		if (implImports.containsKey(name)) {
			ImportDeclaration id = implImports.get(name);
			Node n = id.getChildNodes().get(0).getChildNodes().get(0);

			if (n.toString().equals(packageName)) {
				return;
			}
			
			interfaceImports.add(id);
		}
		else {
			try {
				Class.forName("java.lang." + name);
			}
			catch (ClassNotFoundException e) {
				interfaceImports.add(new ImportDeclaration(implPackageName + name, false, false));
			}
			
		}
	}
	private void deriveImports(Optional<? extends Type> oType) {
		if (oType.isPresent()) {
			deriveImports(oType.get());
		}
	}
	
	private void deriveImports(Type type) {
		
		if (type == null || type.isPrimitiveType() || type.isVoidType()) {
			return;
		}
		
		if (type.isClassOrInterfaceType()) {
			
			ClassOrInterfaceType ct = (ClassOrInterfaceType)type;
			deriveImports(ct.getNameAsString());

			Optional<NodeList<Type>> tas = ct.getTypeArguments();
			if (tas.isEmpty()) {
				return;
			}
			for (Type t : tas.get()) {
				deriveImports(t);
			}
			return;
		}
		
		if (type.isWildcardType()) {
			WildcardType wt = (WildcardType)type;
			deriveImports(wt.getSuperType());
			deriveImports(wt.getExtendedType());
			return;
		}
		
		if (type.isArrayType()) {
			ArrayType at = (ArrayType)type;
			deriveImports(at.getElementType());
			return;
		}
		
		throw new GenerationException("Type " + type + " (" + type.getClass() + ") not supported, yet.");
	}
	
	private void deriveImports(MethodDeclaration implMethod) {
		deriveImports(implMethod.getType());
		
		for (Parameter p : implMethod.getParameters()) {
			deriveImports(p.getType());
		}
	}
	
	private void addBaseInterface(String name) {
		if (name != null && name.length() > 0) {
			baseInterfaces.add(name);
			deriveImports(name);
		}
	}
	
	private void addBaseInterface(ClassOrInterfaceType type) {
		baseInterfaces.add(type.asString());
		deriveImports(type);
	}
	
	private void addServiceSubtype(String baseName, String typeName) {
		Set<String> subtypes = serviceSubtypes.get(baseName);
		
		if (subtypes == null) {
			subtypes = new TreeSet<>();
			serviceSubtypes.put(baseName, subtypes);
		}
		
		subtypes.add(typeName);
	}
	
	private void generatePatternControlMethod(StringBuffer sbBody, AnnotationExpr annotationExpr, String annotationMemberName) {

		if (hasAnnotationAttr(annotationExpr, annotationMemberName)) {
			String methodName = getAnnotationAttrValueAsString(annotationExpr, annotationMemberName);
			if (methodName != null && methodName.length() > 0) {
				sbBody.append("void " + methodName + "();");
			}
		}
	}
	
	//NOTE: order of implemented interfaces: base (inherits), expansions, core (expands), "ordinary" interfaces
	private void generateInterface(ClassOrInterfaceDeclaration implType) {
		
		try {		
			if (!implType.hasModifier(Keyword.PUBLIC)) {
				throw new InvalidTypeException(implType.getNameAsString());
			}
			
			implPackageName = getPackageName(implType);
			String name = implPackageName + implType.getNameAsString();

			String typeName = getInterfaceName(name);
			packageName = getPackageName(typeName);
			String typeNameShort = getShortName(typeName);
			
			implImports = new HashMap<>();
			interfaceImports = new TreeSet<>(new Comparator<ImportDeclaration>() {
				public int compare(ImportDeclaration id1, ImportDeclaration id2) {
					return id1.getNameAsString().compareTo(id2.getNameAsString());
				}				
			});

			baseInterfaces = new ArrayList<>();
			
			CompilationUnit cu = (CompilationUnit)implType.getParentNode().get();
			for (ImportDeclaration id : cu.getImports()) {
				implImports.put(id.getName().getIdentifier(), id);
			}
			
			GenType genType = new GenType();
			
	        StringBuffer sbHeader = genType.getSbHeader();
	        sbHeader.append("package " + packageName + ";");
	        StringBuffer sbDecl = genType.getSbDecl();
	        sbDecl.append("public interface " + typeNameShort);
						
			String baseTypeName = null;
		
			if (hasAnnotationAttr(implType, Service.class, "instantiable")) {
				boolean instantiable = getAnnotationAttrValueAsBoolean(implType, Service.class, "instantiable");
				genType.setInstantiable(instantiable);
			}
			
			if (hasAnnotationAttr(implType, Service.class, "inherits")) {
				baseTypeName = getAnnotationAttrValueAsTypeName(implType, Service.class, "inherits");
				addServiceSubtype(getFullName(baseTypeName), typeName);
			}
			else if (hasAnnotationAttr(implType, Extension.class, "inherits")) {
				baseTypeName = getAnnotationAttrValueAsTypeName(implType, Extension.class, "inherits");
			}

			addBaseInterface(baseTypeName);
			
			
			for (FieldDeclaration implField : implType.getFields()) {
				if (!implField.isAnnotationPresent(Expansion.class)) continue;
				String expansionTypeName = implField.getElementType().asString();
				addBaseInterface(expansionTypeName);
				expansionTypes.add(getFullName(expansionTypeName));
			}
			
			if (hasAnnotationAttr(implType, Extension.class, "extendz")) {
				String coreTypeName = getAnnotationAttrValueAsTypeName(implType, Extension.class, "extendz");
			
				addBaseInterface(coreTypeName);
			}
			
			NodeList<ClassOrInterfaceType> superTypes = implType.getImplementedTypes();
			for (ClassOrInterfaceType superType : superTypes) {
				addBaseInterface(superType);
			}
			
			StringBuffer sbBody = genType.getSbBody();
			
			for (AnnotationExpr annotationExpr : implType.getAnnotations()) {
				String annotationName = getFullName(annotationExpr.getNameAsString());
				if (annotationName.equals(PatternControlMethod.class.getName())) {
					generatePatternControlMethod(sbBody, annotationExpr, "methodName");
				}
			}
			
			for (MethodDeclaration implMethod : implType.getMethods()) {
				if (!implMethod.hasModifier(Keyword.PUBLIC)) {
					continue;
				}
				
				if (implMethod.isAnnotationPresent(Asterisk.class)) {
					continue;
				}
				
				sbBody.append(implMethod.getDeclarationAsString().substring(7));
				sbBody.append(';');				
				deriveImports(implMethod);
				
				for (AnnotationExpr annotationExpr : implMethod.getAnnotations()) {
					
					String annotationName = getFullName(annotationExpr.getNameAsString());
										
					if (systemRuleAnnotations.containsKey(annotationName)) {

						Class<?> ruleAnnotation = systemRuleAnnotations.get(annotationName);
						for (Method method : ruleAnnotation.getDeclaredMethods()) {
							
							if (method.isAnnotationPresent(Generate.class)) {
								generatePatternControlMethod(sbBody, annotationExpr, method.getName());
							}
						}
					}
					else if (appRuleAnnotations.containsKey(annotationName)) {

						AnnotationDeclaration ruleAnnotation = appRuleAnnotations.get(annotationName);
						
						for (BodyDeclaration<?> bodyDeclaration : ruleAnnotation.getMembers()) {
							AnnotationMemberDeclaration annotationMember = (AnnotationMemberDeclaration)bodyDeclaration;
							
							if (annotationMember.isAnnotationPresent(Generate.class)) {
								generatePatternControlMethod(sbBody, annotationExpr, annotationMember.getNameAsString());
							}
						}
					}

				}
			}

			if (implType.getExtendedTypes().size() > 0) {
				String impltypename = implType.getNameAsString();
				sbBody.append(impltypename + " get" + impltypename + "();");
				interfaceImports.add(new ImportDeclaration(name, false, false));
			}
			
			for (ImportDeclaration id : interfaceImports) {
				sbHeader.append(id);
			}
			
			boolean first = true;
			for (String baseInterface : baseInterfaces) {
				if (first) {
					sbDecl.append(" extends ");
					first = false;
				}
				else {
					sbDecl.append(",");
				}
				
				sbDecl.append(baseInterface);
			}
							
			genTypes.put(typeName, genType);
		}
		catch(Throwable e) {
			throw new GenerationException(e, implType.getNameAsString());
		}
	}
	
	private void generateEnums() {
						
		for (String expTypeName : expansionTypes) {
//			System.out.println(expTypeName);
			Set<String> subTypeNames = serviceSubtypes.get(expTypeName);
			if (subTypeNames == null || genTypes.get(expTypeName).isInstantiable()) continue;			
				
			String shortName = getShortName(expTypeName) + "Enum";
			
			GenType enumType = new GenType();
			StringBuffer sbHeader = enumType.getSbHeader();
			sbHeader.append("package " + getPackageName(expTypeName) + ";");
			
			StringBuffer sbDecl = enumType.getSbDecl();
			sbDecl.append("public enum " + shortName);
			
			StringBuffer sbBody = enumType.getSbBody();
			
			boolean first = true;
			for (String subTypeName : subTypeNames) {
				if (first) {
					first = false;
				}
				else {
					sbBody.append(",");
				}
				
				sbBody.append(getShortName(subTypeName));
				
				GenType subType = genTypes.get(subTypeName);
				subType.getSbBody().append(shortName + " get" + shortName +"();");
			}
			
			String name = expTypeName + "Enum";
			genTypes.put(name, enumType);
			
			GenType type = genTypes.get(expTypeName);
			type.getSbBody().append(shortName + " get" + shortName +"();");
		}
	}
	
	/**
	 * Generates component interfaces
	 */
	public void generateInterfaces() {
		
		try {
//			List<ParseResult<CompilationUnit>> prs = sourceRoot.tryToParse();	
			sourceRoot.tryToParse();
		}
		catch(IOException e) {
			throw new GenerationException(e);
		}
//		for (ParseResult<CompilationUnit> pr : prs) {
//			System.out.println(pr.getResult());
//			for (Problem p : pr.getProblems()) {
//				System.out.println("Problem:" + p);
//			}
//		}
		
		genTypes = new HashMap<>();
		expansionTypes = new HashSet<>();
		serviceSubtypes = new HashMap<>();
		
		List<CompilationUnit> units = sourceRoot.getCompilationUnits();
		List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
		
		for (CompilationUnit cu : units) {
			cu.accept(new GenericVisitorAdapter<Void, Void>() {
				public Void visit(AnnotationDeclaration annotation, Void arg) {

					if (annotation.isAnnotationPresent(Rule.class)) {
						String implPackageName = getPackageName(annotation);
						String name = implPackageName + annotation.getNameAsString();
												
						if (implPackageName.startsWith(basePackageName)) {
							appRuleAnnotations.put(name, annotation);
						}
					}
					
					return super.visit(annotation, arg);
				}
			}, null);
		}
		
		for (CompilationUnit cu : units) {
			cu.accept(new GenericVisitorAdapter<Void, Void>() {
				public Void visit(ClassOrInterfaceDeclaration cls, Void arg) {

					if (cls.isAnnotationPresent(Service.class) || cls.isAnnotationPresent(Extension.class)) {
						
						String implPackageName = getPackageName(cls);
												
						if (implPackageName.startsWith(basePackageName)) {
							classes.add(cls);
						}
					}
					
					return super.visit(cls, arg);
				}
			}, null);
		}
		
		for (ClassOrInterfaceDeclaration c : classes) { 
			generateInterface(c);
		}
		
		generateEnums();
		
		for (String typeName : genTypes.keySet()) {
			JavaParser parser = new JavaParser();
	        ParseResult<CompilationUnit> pr = parser.parse(genTypes.get(typeName).toString());
			CompilationUnit cu = pr.getResult().get();
			Path path = targetPath.resolve(typeName.replace(".", FileSystems.getDefault().getSeparator()) + ".java");
			cu.setStorage(path);
			targetRoot.add(cu);
		}
		
		targetRoot.saveAll();
		
		if (classes.size() == 0) {
			System.out.println("Nothing to generate. 0 components found below package '" + basePackageName + "'.");
		}
		else {
			System.out.println(classes.size() + " components found below package '" + basePackageName + "'. Corresponding interfaces successfully generated.");
		}
	}
	
//	public static void main(String[] args) {
//		
//		String inputFolder = "";
//		String basePackageName = "";
//		String outputFolder = "";
//		
//		if (args.length > 0) {
//			inputFolder = args[0];
//		}
//		if (args.length > 1) {
//			basePackageName = args[1];
//		}
//		if (args.length > 2) {
//			outputFolder = args[2];
//		}
//		
////		Generator generator = new ClassBasedGenerator(classesFolder, basePackageName, outputFolder);
////		Generator generator = new SourceBasedGenerator("C:\\Users\\chris\\OneDrive\\Dokumente\\Promotion\\esc\\demolibrary\\src\\main\\java",
////				"de.gwasch.code.demolibrary", "C:\\Users\\chris\\OneDrive\\Dokumente\\Promotion\\esc\\demolibrary\\src\\main\\gen");
//		CodeGenerator generator = new CodeGenerator("C:\\Users\\chris\\OneDrive\\Dokumente\\Promotion\\esc\\demoproductionsystem\\src\\main\\java",
//				"de.gwasch.code.demoproductionsystem", "C:\\Users\\chris\\OneDrive\\Dokumente\\Promotion\\esc\\demoproductionsystem\\src\\main\\gen");
////		Generator generator = new SourceBasedGenerator(inputFolder, basePackageName, outputFolder);
//		generator.generateInterfaces();
//	}
}

package com.dezzy.skorp3.messaging.meta;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.dezzy.skorp3.logging.Logger;

@SuppressWarnings("rawtypes")
public class MessagingAnnotationProcessor {
	
	public static void processMessagingAnnotations() {
		List<Path> paths = getClassfilePaths("target");
		
		String classRoot = "target\\classes\\";
		Class[] classes = loadClasses(classRoot, paths);
		CallbackUserMetaStruct[] callbackUserInfo = getCallbackUserMetaInfo(classes);
	}
	
	private static CallbackUserMetaStruct[] getCallbackUserMetaInfo(final Class[] classes) {
		CallbackUserMetaStruct[] cbUsers = new CallbackUserMetaStruct[classes.length];
		
		for (int i = 0; i < cbUsers.length; i++) {
			cbUsers[i] = new CallbackUserMetaStruct();
			cbUsers[i].classType = classes[i];
			cbUsers[i].callbacks = findAnnotations(classes[i]);
			//TODO: Remove this test
			if (cbUsers[i].callbacks.length != 0) {
				System.out.println(cbUsers[i].callbacks[0].handlesWhat);
				System.out.println(classes[i]);
			}
			//
		}
		
		return cbUsers;
	}
	
	private static CallbackMetaStruct[] findAnnotations(final Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		List<Annotation> handlesAnnotations = new ArrayList<Annotation>();
		List<Annotation> forAnnotations = new ArrayList<Annotation>();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Annotation handlesAnnotation = method.getAnnotation(Handles.class);
			
			if (handlesAnnotation != null) {
				handlesAnnotations.add(handlesAnnotation);
				
				Annotation forAnnotation = method.getAnnotation(For.class);
				if (forAnnotation != null) {
					forAnnotations.add(forAnnotation);
				} else {
					forAnnotations.add(null);
				}
			}
		}
		
		return compileAnnotationLists(handlesAnnotations, forAnnotations);
	}
	
	private static CallbackMetaStruct[] compileAnnotationLists(final List<Annotation> handlesAnnotations, final List<Annotation> forAnnotations) {
		CallbackMetaStruct[] cbInfo = new CallbackMetaStruct[handlesAnnotations.size()];
		
		for (int i = 0; i < cbInfo.length; i++) {
			cbInfo[i].handlesWhat = handlesAnnotations.get(i);
			cbInfo[i].forWho = forAnnotations.get(i);
		}
		
		return cbInfo;
	}
	
	private static URLClassLoader createClassLoader(final String classRoot) {
		URLClassLoader classLoader = null;
		
		try {
			classLoader = new URLClassLoader(new URL[] {new File(classRoot).toURI().toURL()});
		} catch (MalformedURLException e) {
			e.printStackTrace();
			e.printStackTrace(Logger.getLogger());
			Logger.error("Unable to create ClassLoader");
		}
		
		return classLoader;
	}
	
	private static Class[] loadClasses(final String classRoot, final List<Path> paths) {
		URLClassLoader classLoader = createClassLoader(classRoot);
		
		Class[] classes = new Class[paths.size()];
		
		for (int i = 0; i < classes.length; i++) {
			String fullyQualifiedName = getFQClassName(classRoot, paths.get(i).toString());
			try {
				classes[i] = classLoader.loadClass(fullyQualifiedName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				e.printStackTrace(Logger.getLogger());
				Logger.error("Unable to load class \"" + fullyQualifiedName +"\"");
			}
		}
		
		try {
			classLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace(Logger.getLogger());
			Logger.error("Unable to close ClassLoader");
		}
		
		return classes;
	}
	
	private static List<Path> getClassfilePaths(final String fromDir) {
		List<Path> paths = new ArrayList<Path>();
		
		try {
			Files.walk(Paths.get(fromDir))
				.filter(Files::isRegularFile)
				.forEach(paths::add);
			
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(Logger.getLogger());
		}
		
		return paths;
	}
	
	/**
	 * Returns the fully qualified class name for a given path to a class file and a root directory.
	 * The root directory and path must use backslashes instead of forward slashes.
	 * The root directory must also include two backslashes at the end, if it is not the current directory.
	 * 
	 * @param root root to cut off from path when constructing FQ name
	 * @param path path to class file
	 * @return fully qualified class name
	 */
	private static String getFQClassName(final String root, final String path) {
		//Remove root from front of path and ".class" from end of path
		String trimmed = path.substring(path.indexOf(root) + root.length(), path.indexOf(".class"));
		
		//Replace backslashes with periods
		return trimmed.replaceAll(Matcher.quoteReplacement("\\"), ".");
	}
	
	private static class CallbackMetaStruct {
		private Annotation handlesWhat;
		private Annotation forWho;
	}
	
	private static class CallbackUserMetaStruct {
		private Class classType;
		private CallbackMetaStruct[] callbacks;
	}
}

package com.dezzy.skorp3.messaging.meta;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.dezzy.skorp3.logging.Logger;

@SuppressWarnings("rawtypes")
public class MessagingAnnotationProcessor {
	
	public static void processMessagingAnnotations() {
		List<Path> paths = getClassfilePaths("target");
		
		String classRoot = "target\\classes\\";
		Class[] classes = loadClasses(classRoot, paths);
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
				//.filter(p -> p.endsWith(".class"))
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
}

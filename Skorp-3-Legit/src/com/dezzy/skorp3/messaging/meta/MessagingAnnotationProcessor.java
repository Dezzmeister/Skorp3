package com.dezzy.skorp3.messaging.meta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import com.dezzy.skorp3.logging.Logger;

@SuppressWarnings("rawtypes")
public class MessagingAnnotationProcessor {
	
	public static void processMessagingAnnotations() {
		List<Path> paths = getClassfilePaths("target");
		
		String classRoot = "target\\classes\\";
		Class[] classes = loadClasses(classRoot, paths);
		CallbackUserMetaStruct[] callbackUserInfo = getCallbackUserMetaInfo(classes);
		System.out.println(callbackUserInfo.length);
	}
	
	private static String constructFileString(CallbackUserMetaStruct[] cbUserInfo) {
		StringBuilder builder = new StringBuilder("Skorp 3 Messaging System Outline");
		
		return builder.toString();
	}
	
	private static SenderUserMetaStruct[] getSenderUserMetaInfo(final Class[] classes) {
		SenderUserMetaStruct[] senderUsers = new SenderUserMetaStruct[classes.length];
		
		for (int i = 0; i < senderUsers.length; i++) {
			senderUsers[i] = new SenderUserMetaStruct();
			senderUsers[i].classType = classes[i];
			senderUsers[i].senders = findSenderAnnotations(classes[i]);
		}
		
		//TODO: Write filter function
		return senderUsers;
	}
	
	private static SenderMetaStruct[] findSenderAnnotations(final Class clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		
		List<Sends> sendsAnnotations = new ArrayList<Sends>();
		List<SendsTo> toAnnotations = new ArrayList<SendsTo>();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Sends sendsAnnotation = method.getAnnotation(Sends.class);
			
			if (sendsAnnotation != null) {
				sendsAnnotations.add(sendsAnnotation);
				
				SendsTo toAnnotation = method.getAnnotation(SendsTo.class);
				if (toAnnotation != null) {
					toAnnotations.add(toAnnotation);
				} else {
					toAnnotations.add(null);
				}
			}
		}
		
		//TODO: finish this
		return null;
	}
	
	private static CallbackUserMetaStruct[] getCallbackUserMetaInfo(final Class[] classes) {
		CallbackUserMetaStruct[] cbUsers = new CallbackUserMetaStruct[classes.length];
		
		for (int i = 0; i < cbUsers.length; i++) {
			cbUsers[i] = new CallbackUserMetaStruct();
			cbUsers[i].classType = classes[i];
			cbUsers[i].callbacks = findCallbackAnnotations(classes[i]);
		}
		
		return filterMessageUsers(cbUsers);
	}
	
	private static CallbackUserMetaStruct[] filterMessageUsers(final CallbackUserMetaStruct[] callbackUsers) {
		return Arrays.stream(callbackUsers).filter(cb -> cb.callbacks.length != 0).toArray(CallbackUserMetaStruct[]::new);
	}
	
	private static CallbackMetaStruct[] findCallbackAnnotations(final Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		
		List<Handles> handlesAnnotations = new ArrayList<Handles>();
		List<HandlesFor> forAnnotations = new ArrayList<HandlesFor>();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Handles handlesAnnotation = method.getAnnotation(Handles.class);
			
			if (handlesAnnotation != null) {
				handlesAnnotations.add(handlesAnnotation);
				
				HandlesFor forAnnotation = method.getAnnotation(HandlesFor.class);
				if (forAnnotation != null) {
					forAnnotations.add(forAnnotation);
				} else {
					forAnnotations.add(null);
				}
			}
		}
		
		return compileCallbackAnnotationLists(handlesAnnotations, forAnnotations);
	}
	
	private static CallbackMetaStruct[] compileCallbackAnnotationLists(final List<Handles> handlesAnnotations, final List<HandlesFor> forAnnotations) {
		CallbackMetaStruct[] cbInfo = new CallbackMetaStruct[handlesAnnotations.size()];
		
		for (int i = 0; i < cbInfo.length; i++) {
			cbInfo[i] = new CallbackMetaStruct();
			
			cbInfo[i].handlesWhat = handlesAnnotations.get(i);
			cbInfo[i].handlesWhatForWho = forAnnotations.get(i);
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
		private Handles handlesWhat;
		private HandlesFor handlesWhatForWho;
	}
	
	private static class CallbackUserMetaStruct {
		private Class classType;
		private CallbackMetaStruct[] callbacks;
	}
	
	private static class SenderMetaStruct {
		private Sends sendsWhat;
		private SendsTo sendsWhatToWho;
	}
	
	private static class SenderUserMetaStruct {
		private Class classType;
		private SenderMetaStruct[] senders;
	}
}

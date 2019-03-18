package com.dezzy.skorp3.messaging.meta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.dezzy.skorp3.logging.Logger;

public class MessagingAnnotationProcessor {
	
	public static void processMessagingAnnotations() {
		List<Path> paths = getClassfilePaths("target");
		
		for (Path path : paths) {
			//TODO: Write this
		}
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
}

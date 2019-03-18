package com.dezzy.skorp3.meta;

import com.dezzy.skorp3.messaging.meta.MessagingAnnotationProcessor;

public class MetaUtilities {
	static {
		MessagingAnnotationProcessor.processMessagingAnnotations();
	}
	
	public static void init() {
		
	}
}

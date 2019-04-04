package com.dezzy.skorp3.meta;

import com.dezzy.skorp3.messaging.meta.MessagingAnnotationProcessor;

public class MetaUtilities {
	
	public static void init() {
		MessagingAnnotationProcessor.processMessagingAnnotations();
	}
}

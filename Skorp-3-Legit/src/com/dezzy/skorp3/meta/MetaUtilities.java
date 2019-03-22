package com.dezzy.skorp3.meta;

import com.dezzy.skorp3.messaging.meta.MessagingAnnotationProcessor;

public class MetaUtilities extends RequiresForcedLoading {
	static {
		MessagingAnnotationProcessor.processMessagingAnnotations();
	}
}

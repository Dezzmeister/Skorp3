package com.dezzy.skorp3.messaging.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the messages that a message callback function handles. This is used by 
 * {@link com.dezzy.skorp3.messaging.meta.MessagingAnnotationProcessor HandlesAnnotationProcessor}
 * to generate a file containing a list of all classes that handle messages, the names of their callback
 * functions, the message handler each callback function handles messages for, and the messages each callback
 * function handles.
 *
 * @author Joe Desmond
 */
@Retention(CLASS)
@Target(METHOD)
public @interface Handles {
	String[] value();
}

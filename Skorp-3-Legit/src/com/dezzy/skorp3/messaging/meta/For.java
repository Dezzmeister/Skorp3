package com.dezzy.skorp3.messaging.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the message handler that a callback function handles messages for.
 * 
 *
 * @author Joe Desmond
 * @see com.dezzy.skorp3.messaging.meta.Handles Handles
 */
@Retention(CLASS)
@Target(METHOD)
public @interface For {
	String value();
}

package com.dezzy.skorp3.messaging.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the messages that a method sends through a message handler.
 *
 * @author Joe Desmond
 * @see com.dezzy.skorp3.messaging.meta.Handles Handles
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Sends {
	String[] value();
}

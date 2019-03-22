package com.dezzy.skorp3.messaging.meta;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specified the message handlers that a method sends messages to. Should be used
 * with {@link Sends}.
 * <p>
 * <b>NOTE:</b> Use "Global" to specify all message handlers (global dispatch).
 *
 * @author Joe Desmond
 * @see Handles
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface SendsTo {
	String[] value();
}

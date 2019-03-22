package com.dezzy.skorp3.messaging;

/**
 * Used with {@link MessageHandler}. MessageCallbackFuncs can be specified to receive messages
 * and registered with a MessageHandler.
 * <p>
 * <b>NOTE:</b> When defining a message callback function, use the {@link com.dezzy.skorp3.messaging.meta.Handles Handles}
 * and {@link com.dezzy.skorp3.messaging.meta.HandlesFor HandlesFor} annotations.
 *
 * @author Joe Desmond
 */
@FunctionalInterface
public interface MessageCallbackFunc {
	
	void handleMessage(Message message);
}

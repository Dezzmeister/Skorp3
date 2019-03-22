package com.dezzy.skorp3.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dezzy.skorp3.logging.Logger;

/**
 * Represents a named messaging system, consisting of callback functions. Messages can be posted to this system and they will be handled by the 
 * callback functions.
 *
 * @author Joe Desmond
 */
public final class MessageHandler {
	private static final Map<String, MessageHandler> MESSAGE_HANDLERS = new HashMap<String, MessageHandler>();
	private final List<MessageCallbackFunc> messageReceivers = new ArrayList<MessageCallbackFunc>();
	
	public MessageHandler(final String handlerName) {
		MESSAGE_HANDLERS.put(handlerName, this);
	}
	
	/**
	 * Creates and registers a MessageHandler with the name <code>handlerName</code>.
	 * 
	 * @param handlerName name of the new MessageHandler
	 * @return this MessageHandler
	 */
	public static MessageHandler createHandler(final String handlerName) {
		return new MessageHandler(handlerName);
	}	
	
	/**
	 * Registers a callback function for an existing message handler.
	 * 
	 * @param handlerName name of the message handler
	 * @param callback callback function
	 */
	public static void registerCallback(final String handlerName, final MessageCallbackFunc callback) {
		MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.add(callback);
		}
	}
	
	/**
	 * Dispatches a message to a message handler. All callback functions registered on the message handler will receive the message.
	 * 
	 * @param handlerName name of the message handler
	 * @param message message to send
	 */
	public static void dispatch(final String handlerName, final Message message) {
		final MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.forEach(mcf -> mcf.handleMessage(message));
		}
	}
	
	/**
	 * Dispatches a message to all message handlers. All registered callback functions will receive the message.
	 * 
	 * @param message message to send
	 */
	public static void dispatchGlobal(final Message message) {
		MESSAGE_HANDLERS.entrySet()
			.forEach(e -> e.getValue().messageReceivers
					.forEach(mcf -> mcf.handleMessage(message)));
	}
}

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
	private final Map<MessageCallbackFunc, DesignatedCaller> designatedCallers = new HashMap<MessageCallbackFunc, DesignatedCaller>();
	
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
	 * Registers a callback function for an existing message handler. When a message is dispatched to this handler, the callback function
	 * will be run by the designated caller instead of whichever thread dispatched the message.
	 * 
	 * @param handlerName name of the message handler
	 * @param callback callback function
	 * @param caller designated caller
	 */
	public static void registerCallback(final String handlerName, final MessageCallbackFunc callback, final DesignatedCaller caller) {
		MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.add(callback);
			handler.designatedCallers.put(callback, caller);
		}
	}
	
	/**
	 * Registers a callback function for this message handler.
	 * 
	 * @param callback callback function
	 */
	public void registerCallback(final MessageCallbackFunc callback) {
		messageReceivers.add(callback);
	}
	
	/**
	 * Registers a callback function for this message handler. When a message is dispatched to this handler, the callback function
	 * will be run by the designated caller instead of whichever thread dispatched the message.
	 * 
	 * @param callback callback function
	 * @param caller designated caller
	 */
	public void registerCallback(final MessageCallbackFunc callback, final DesignatedCaller caller) {
		messageReceivers.add(callback);
		designatedCallers.put(callback, caller);
	}
	
	/**
	 * Dispatches a message to a message handler. All callback functions registered on the message handler will receive the message. If
	 * any callback function has a designated caller, that caller will be used to run the callback function.
	 * 
	 * @param handlerName name of the message handler
	 * @param message message to send
	 */
	public static void dispatch(final String handlerName, final Message message) {
		final MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.forEach(mcf -> {
				DesignatedCaller caller = handler.designatedCallers.get(mcf);
				if (caller == null) {
					mcf.handleMessage(message);
				} else {
					caller.call(mcf, message);
				}
			});
		}
	}
	
	/**
	 * Dispatches a message to this message handler. All callback functions registered on this message handler will receive the message. If
	 * any callback function has a designated caller, that caller will be used to run the callback function.
	 * 
	 * @param message message to send
	 */
	public void dispatch(final Message message) {
		messageReceivers.forEach(mcf -> {
			DesignatedCaller caller = designatedCallers.get(mcf);
			
			if (caller == null) {
				mcf.handleMessage(message);
			} else {
				caller.call(mcf, message);
			}
		});
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

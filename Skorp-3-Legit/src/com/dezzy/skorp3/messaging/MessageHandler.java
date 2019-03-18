package com.dezzy.skorp3.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dezzy.skorp3.logging.Logger;

public final class MessageHandler {
	private static final Map<String, MessageHandler> MESSAGE_HANDLERS = new HashMap<String, MessageHandler>();
	private final List<MessageCallbackFunc> messageReceivers = new ArrayList<MessageCallbackFunc>();
	
	public MessageHandler(final String handlerName) {
		MESSAGE_HANDLERS.put(handlerName, this);
	}
	
	public static MessageHandler createHandler(final String handlerName) {
		return new MessageHandler(handlerName);
	}
	
	
	
	public static void registerCallback(final String handlerName, final MessageCallbackFunc callback) {
		MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.add(callback);
		}
	}
	
	public static void dispatch(final String handlerName, final Message message) {
		final MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.forEach(mcf -> mcf.handleMessage(message));
		}
	}
	
	public static void dispatchGlobal(final Message message) {
		MESSAGE_HANDLERS.entrySet()
			.forEach(e -> e.getValue().messageReceivers
					.forEach(mcf -> mcf.handleMessage(message)));
	}
}

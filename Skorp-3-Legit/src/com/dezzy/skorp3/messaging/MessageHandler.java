package com.dezzy.skorp3.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dezzy.skorp3.logging.Logger;

public class MessageHandler {
	private static final Map<String, MessageHandler> MESSAGE_HANDLERS = new HashMap<String, MessageHandler>();
	private final List<MessageCallbackFunc> messageReceivers = new ArrayList<MessageCallbackFunc>();
	
	public MessageHandler(String handlerName) {
		MESSAGE_HANDLERS.put(handlerName, this);
	}
	
	public static void registerCallback(String handlerName, MessageCallbackFunc callback) {
		MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.add(callback);
		}
	}
	
	public static void dispatch(String handlerName, Message message) {
		MessageHandler handler = MESSAGE_HANDLERS.get(handlerName);
		if (handler == null) {
			Logger.error("MessageHandler \"" + handlerName + "\" does not exist!");
		} else {
			handler.messageReceivers.forEach(mc -> mc.handleMessage(message));
		}
	}
}

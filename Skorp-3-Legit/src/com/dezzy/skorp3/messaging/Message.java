package com.dezzy.skorp3.messaging;

/**
 * Contains text and data. Messages can be sent through {@link MessageHandler MessageHandlers} and received by {@link MessageCallbackFunc Callback Functions}.
 *
 * @author Joe Desmond
 */
public final class Message {
	public final String messageText;
	public final Object data;
	
	public Message(final String _messageText, final Object _data) {
		messageText = _messageText;
		data = _data;
	}
}

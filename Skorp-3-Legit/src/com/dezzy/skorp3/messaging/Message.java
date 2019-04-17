package com.dezzy.skorp3.messaging;

/**
 * Contains text and data. Messages can be sent through {@link MessageHandler MessageHandlers} and received by {@link MessageCallbackFunc Callback Functions}.
 *
 * @author Joe Desmond
 */
public final class Message {
	public final String messageText;
	public final Object data;
	
	/**
	 * Creates a Message object with the specified text identifier and the specified data. The data will be passed to a message handler for the text identifier.
	 * 
	 * @param _messageText text identifier
	 * @param _data message data
	 */
	public Message(final String _messageText, final Object _data) {
		messageText = _messageText;
		data = _data;
	}
}

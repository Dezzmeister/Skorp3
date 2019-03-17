package com.dezzy.skorp3.messaging;


public final class Message {
	public final String message;
	public final Object data;
	
	public Message(final String _message, final Object _data) {
		message = _message;
		data = _data;
	}
}

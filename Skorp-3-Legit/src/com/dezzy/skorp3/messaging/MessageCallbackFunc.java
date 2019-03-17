package com.dezzy.skorp3.messaging;

@FunctionalInterface
public interface MessageCallbackFunc {
	
	void handleMessage(Message message);
}

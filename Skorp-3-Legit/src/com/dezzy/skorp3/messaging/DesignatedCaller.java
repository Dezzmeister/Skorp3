package com.dezzy.skorp3.messaging;


public class DesignatedCaller implements Runnable {
	volatile boolean isRunning = false;
	volatile MessageCallbackFunc function;
	volatile Message message;
	volatile boolean updated = false;
	
	public synchronized void call(final MessageCallbackFunc func, final Message msg) {
		function = func;
		message = msg;
		updated = true;
	}
	
	@Override
	public void run() {
		while (isRunning) {
			if (updated) {
				function.handleMessage(message);
			}
		}
	}
	
}

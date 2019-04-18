package com.dezzy.skorp3.messaging;

import java.util.concurrent.BlockingQueue;

import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.meta.Handles;
import com.dezzy.skorp3.messaging.meta.HandlesFor;

public abstract class DesignatedCaller implements Runnable {
	protected volatile boolean isRunning = true;
	private BlockingQueue<CallTask> taskQueue;
	
	protected DesignatedCaller(final BlockingQueue<CallTask> _taskQueue) {
		taskQueue = _taskQueue;
	}
	
	@Handles("QUIT")
	@HandlesFor("Control")
	protected void handleQuit(final Message message) {
		if (message.messageText.equals("QUIT")) {
			stop();
		}
	}
	
	protected void stop() {
		isRunning = false;
	}
	
	public synchronized void call(final MessageCallbackFunc func, final Message msg) {
		try {
			taskQueue.put(new CallTask(func, msg));
		} catch (InterruptedException e) {
			Logger.error("Problem adding object to internal Queue of a DesignatedCaller!");
			e.printStackTrace(Logger.getLogger());
		}
	}
	
	@Override
	public void run() {
		while (isRunning) {
			try {
				CallTask task = taskQueue.take();
				task.execute();
			} catch (InterruptedException e) {
				Logger.error("Problem taking object from internal Queue of a DesignatedCaller!");
				e.printStackTrace(Logger.getLogger());
			}
			
		}
	}
	
	protected class CallTask {
		private final MessageCallbackFunc function;
		private final Message message;
		
		public CallTask(final MessageCallbackFunc _function, final Message _message) {
			function = _function;
			message = _message;
		}
		
		public void execute() {
			function.handleMessage(message);
		}
	}
}

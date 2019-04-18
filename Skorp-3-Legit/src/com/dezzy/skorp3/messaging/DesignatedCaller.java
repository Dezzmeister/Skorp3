package com.dezzy.skorp3.messaging;

import java.util.concurrent.BlockingQueue;

import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.meta.Handles;
import com.dezzy.skorp3.messaging.meta.HandlesFor;

/**
 * Works with a {@link MessageHandler} to handle messages on a different thread than the dispatcher's.
 *
 * @author Joe Desmond
 */
public abstract class DesignatedCaller implements Runnable {
	protected volatile boolean isRunning = true;
	private BlockingQueue<CallTask> taskQueue;
	
	/**
	 * Creates a DesignatedCaller with the specified BlockingQueue.
	 * 
	 * @param _taskQueue BlockingQueue to hold queued message handler calls
	 */
	protected DesignatedCaller(final BlockingQueue<CallTask> _taskQueue) {
		taskQueue = _taskQueue;
		MessageHandlerRegistry.CONTROL_HANDLER.registerCallback(this::handleQuit);
	}
	
	@Handles("QUIT")
	@HandlesFor("Control")
	protected void handleQuit(final Message message) {
		if (message.messageText.equals("QUIT")) {
			stop();
		}
	}
	
	/**
	 * Stops this DesignatedCaller's thread.
	 */
	protected void stop() {
		isRunning = false;
	}
	
	/**
	 * Queues a function to be called on a message in this DesignatedCaller's thread.
	 * 
	 * @param func callback function
	 * @param msg message parameter for callback function
	 */
	synchronized void call(final MessageCallbackFunc func, final Message msg) {
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
			while (!taskQueue.isEmpty()) {
				CallTask task = taskQueue.remove();
				task.execute();
			}
			
			runOtherTasks();
		}
	}
	
	/**
	 * This method is run after the internal message queue is checked and all messages are handled.
	 */
	protected abstract void runOtherTasks();
	
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

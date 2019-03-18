package com.dezzy.skorp3.test.messaging;

import com.dezzy.skorp3.logging.Logger;
import com.dezzy.skorp3.messaging.Message;
import com.dezzy.skorp3.messaging.MessageHandler;
import com.dezzy.skorp3.messaging.meta.For;
import com.dezzy.skorp3.messaging.meta.Handles;
import com.dezzy.skorp3.test.graphics.WindowTest;

public class MessageTest {
	private static volatile boolean run = true;
	
	public static void globalMessageTest() {
		new Thread(new WindowTest()).start();
		MessageHandler.createHandler("Control");
		MessageHandler.registerCallback("Control", MessageTest::controlCallbackFunc);
		
		while(run);
		Logger.log("MessageTest is quitting");
	}
	
	@Handles({"QUIT"})
	@For("Control")
	private static void controlCallbackFunc(Message message) {
		Logger.log(message.message + " message received by MessageTest.");
		if (message.message.equals("QUIT")) {
			run = false;
		}
	}
}

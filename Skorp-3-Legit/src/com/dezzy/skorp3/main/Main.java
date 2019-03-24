package com.dezzy.skorp3.main;

import com.dezzy.skorp3.meta.Startup;
import com.dezzy.skorp3.test.math.MatrixTest;
import com.dezzy.skorp3.test.messaging.MessageTest;

public class Main {
	
	public static void main(String[] args) {
		Startup.start();
		//MatrixTest.testTransformations();
		MessageTest.globalMessageTest();
	}
	
}

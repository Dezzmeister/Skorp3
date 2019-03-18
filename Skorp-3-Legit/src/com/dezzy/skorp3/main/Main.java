package com.dezzy.skorp3.main;

import com.dezzy.skorp3.meta.MetaUtilities;
import com.dezzy.skorp3.test.messaging.MessageTest;

public class Main {
	
	public static void main(String[] args) {
		MetaUtilities.init();		
		MessageTest.globalMessageTest();		
	}
	
}

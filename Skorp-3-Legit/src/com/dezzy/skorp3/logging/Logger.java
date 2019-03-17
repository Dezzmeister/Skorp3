package com.dezzy.skorp3.logging;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static final PrintStream log;
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
			
	static {
		PrintStream ps = null;
		
		try {
			ps = new PrintStream(new File("logs/log.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log = ps;
		log("Logger created.");
	}
	
	public static void log(final Object o) {
		log.println(date() + o);
		System.out.println(date() + o);
	}
	
	public static void error(final Object o) {
		log.println(date() + "ERROR: " + o);
		System.err.println(date() + o);
	}
	
	public static PrintStream getLogger() {
		return log;
	}
	
	private static String date() {
		return timeFormat.format(new Date()) + "\t";
	}
}

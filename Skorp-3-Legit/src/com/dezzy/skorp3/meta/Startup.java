package com.dezzy.skorp3.meta;

/**
 * Handles preliminary tasks before the game actually begins.
 *
 * @author Joe Desmond
 */
public class Startup {
	
	/**
	 * Loads classes that perform independent, static functions on startup and need to be forcibly loaded by calling their <code>init()</code> methods.
	 */
	public static void start() {
		MetaUtilities.init();
		//MessageHandlerRegistry.init();
	}
}

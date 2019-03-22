package com.dezzy.skorp3.meta;

/**
 * Extended by any class that acts independently and needs to be forcibly loaded.
 *
 * @author Joe Desmond
 */
public class RequiresForcedLoading {
	
	/**
	 * Forces the classloader to load this class.
	 */
	public static void init() {}
}

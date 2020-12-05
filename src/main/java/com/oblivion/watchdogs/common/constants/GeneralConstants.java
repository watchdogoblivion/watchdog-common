package com.oblivion.watchdogs.common.constants;

/**
 * @author Samuel D. This class is for constants that can be used in any place.
 */
public class GeneralConstants {

	/*********************** Delimiters ***********************/
	public static String D1 = " - ";
	public static String D2 = ", ";
	public static String D3 = " ";
	public static String D4 = ": ";
	public static String D5 = " ::: ";

	/*********************** Method logging ***********************/
	public static final String ARG = "{}";

	/************************ CONSOLE PRINTING ***********************/
	public static final String OUT = "OUT";
	public static final String ERROR_OUT = "ERROR OUT";

	/************************ DATE FORMAT ***********************/
	public static String GLOBAL_CONSOLE_LOG_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss,SSS";
	
	/**
	 * Hiding no default constructor since this is a statically used class
	 */
	private GeneralConstants() {
	}

}
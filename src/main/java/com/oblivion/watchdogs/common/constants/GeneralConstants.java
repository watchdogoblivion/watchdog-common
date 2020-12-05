package com.oblivion.watchdogs.common.constants;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Samuel D. This class is for constants that can be used in any place.
 */
@Slf4j
@Component("GeneralConstants")
public class GeneralConstants {

	/*********************** Delimiters ***********************/
	public static String D1 = " - ";
	public static String D2 = ", ";
	public static String D3 = " ";
	public static String D4 = ": ";
	public static String D5 = " ::: ";

	/*********************** Method logging ***********************/
	public static final String START_METHOD = "Initiating method {}";
	public static final String END_METHOD = "Terminating method: {}";
	public static final String METHOD_EXCEPTION = "An Exception was thrown before the method could finish executing";
	public static final String EXECUTION_MS = "Completed in {}ms";
	public static final String VOID = "void";
	public static final String EMPTY = "";
	public static final String WITH_ARGS = "With arguments: {}";
	public static final String WITH_RESPONSE = "With Response: {}";
	public static final String ARG = "{}";

	/*********************** package filters ***********************/
	public static final String JAVA_UTIL = "java.util";
	public static final String JAVA_LANG = "java.lang";

	/************************ CONSOLE PRINTING ***********************/
	public static final String OUT = "OUT";
	public static final String ERROR_OUT = "ERROR OUT";

	/************************ CONTEXT INFO ***********************/
	public static final String SERVICE = "Service";

	/************************ DATE FORMAT ***********************/
	public static String GLOBAL_CONSOLE_LOG_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss,SSS";

	/************************ Root package ***********************/
	public static final String COMMON_ROOT = "com.oblivion.watchdogs.common";
	@Value("${root:#{null}}")
	public String rootPackage;
	public static String root;

	/**
	 * Hiding no default constructor since this is a statically used class
	 */
	private GeneralConstants() {
	}

	/**
	 * This method sets the auto wired field values, to corresponding static fields
	 * so that this class can act statically.
	 */
	@PostConstruct
	private void listen() {
		root = rootPackage;
		log.info("root: {}", root);
	}

}
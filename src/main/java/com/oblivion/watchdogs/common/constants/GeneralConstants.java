package com.oblivion.watchdogs.common.constants;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Samuel D. This class is for constants that can be used in any place.
 */
@Slf4j
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
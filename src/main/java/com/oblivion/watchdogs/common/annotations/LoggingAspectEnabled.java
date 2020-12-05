package com.oblivion.watchdogs.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Samuel D. This is a custom annotation to include methods and types
 *         (classes, interfaces, and enums) in the logging aspect.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface LoggingAspectEnabled {

	/**
	 * The log level indicates which log will be used in
	 * com.cvs.opportunity.common.logger. If this level is OFF then you cannot
	 * select the levels for args and response
	 *
	 * @return LogLevel
	 */
	LogLevel baseLevel() default LogLevel.OFF;

	/**
	 * This determines if the method arguments will be logged at a certain level
	 *
	 * @return boolean
	 */
	LogLevel argsLevel() default LogLevel.OFF;

	/**
	 * This determines if the method response will be logged at a certain level
	 *
	 * @return boolean
	 */
	LogLevel responseLevel() default LogLevel.OFF;

}
package com.oblivion.watchdogs.common.logger;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Samuel D. Class to log exceptions in JSON format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JSONExceptionLogger {

	/**
	 * 
	 */
	private String rootExceptionMessage;

	/**
	 * 
	 */
	private String concludingMethodSignature;

	/**
	 * 
	 */
	private List<ExceptionLog> exceptionLogs;

	/**
	 * 
	 */
	private Map<String, Object> additionalInfo;

}
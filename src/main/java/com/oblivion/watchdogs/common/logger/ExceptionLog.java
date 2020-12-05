package com.oblivion.watchdogs.common.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 * @author Samuel D.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionLog {

	/**
	 * 
	 */
	private String exception;

	/**
	 * 
	 */
	private List<String> originatingIDELocators;

}
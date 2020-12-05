package com.oblivion.watchdogs.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Samuel D.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Builder
public class ApiSubError {

	/**
	 * 
	 */
	private String object;

	/**
	 * 
	 */
	private String field;

	/**
	 * 
	 */
	private Object rejectedValue;

	/**
	 * 
	 */
	private String message;

}
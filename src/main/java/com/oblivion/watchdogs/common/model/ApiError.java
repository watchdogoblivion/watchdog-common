package com.oblivion.watchdogs.common.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApiError {

	/**
	 * 
	 */
	@JsonFormat(shape = Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
	private Date date;

	/**
	 * 
	 */
	private String statusPhrase;

	/**
	 * 
	 */
	private int statusCode;

	/**
	 * 
	 */
	private String exceptionName;

	/**
	 * 
	 */
	private String debugMessage;

	/**
	 * 
	 */
	private List<ApiSubError> subErrors;

	/**
	 * 
	 */
	private Map<String, Object> additionalInfo;

	/**
	 * 
	 * @param date
	 * @param statusPhrase
	 * @param statusCode
	 * @param debugMessage
	 * @param subErrors
	 */
	public ApiError(Date date, String statusPhrase, int statusCode, String debugMessage, List<ApiSubError> subErrors) {
		this.date = date;
		this.statusPhrase = statusPhrase;
		this.statusCode = statusCode;
		this.debugMessage = debugMessage;
		this.subErrors = subErrors;
	}

}
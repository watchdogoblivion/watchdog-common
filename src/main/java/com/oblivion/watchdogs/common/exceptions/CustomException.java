package com.oblivion.watchdogs.common.exceptions;

import com.oblivion.watchdogs.common.model.ApiError;

/**
 * @author Samuel D.
 *
 */
public interface CustomException {

	/**
	 * Api error used in responses
	 * 
	 * @return
	 */
	ApiError getEnclosedError();

}
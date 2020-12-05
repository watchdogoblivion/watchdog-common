package com.oblivion.watchdogs.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * @author Samuel D.
 *
 */
public abstract class ResponseHandler extends ResponseEntityExceptionHandler {

	/**
	 * 
	 * @param <D>
	 * @param data
	 * @return
	 */
	protected <D> ResponseEntity<D> ok(D data) {
		return new ResponseEntity<D>(data, HttpStatus.OK);
	}

	/**
	 * 
	 * @param <D>
	 * @param error
	 * @return
	 */
	protected <D> ResponseEntity<D> notFound(D error) {
		return new ResponseEntity<D>(error, HttpStatus.NOT_FOUND);
	}

	/**
	 * 
	 * @param <D>
	 * @param error
	 * @return
	 */
	protected <D> ResponseEntity<D> badRequest(D error) {
		return new ResponseEntity<D>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * @param <D>
	 * @param error
	 * @return
	 */
	protected <D> ResponseEntity<D> errorFound(D error) {
		return new ResponseEntity<D>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * @param <D>
	 * @param error
	 * @return
	 */
	protected <D> ResponseEntity<D> timeOut(D error) {
		return new ResponseEntity<D>(error, HttpStatus.GATEWAY_TIMEOUT);
	}

}
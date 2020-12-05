package com.oblivion.watchdogs.common.aop;

import static com.oblivion.watchdogs.common.logger.Log.error;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.oblivion.watchdogs.common.exceptions.CustomException;
import com.oblivion.watchdogs.common.exceptions.ResponseHandler;
import com.oblivion.watchdogs.common.model.ApiError;
import com.oblivion.watchdogs.common.utility.GenericUtility;

/**
 * 
 * @author Samuel
 *
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest webRequest) {
		return getResponseEntity(ex, status, webRequest);
	}

	/**
	 * Handler for all general Exceptions, issues may occur if they are replaced
	 * with Exception.class
	 *
	 * @param ex
	 * @param request
	 * @return corresponding response entity with APIError format.
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ IllegalArgumentException.class, NullPointerException.class,
			ArrayIndexOutOfBoundsException.class, ParseException.class, SQLException.class, IOException.class })
	public ResponseEntity<Object> generalInternalServerErrorHandler(Exception ex, HttpServletRequest request) {
		Throwable cause = ex.getCause();
		if (cause == null) {
			error(this, "GeneralInternalServerErrorHandler: {}, Message: {} ", ex.getClass().getSimpleName(),
					ex.getMessage());
			return getResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
		}
		error(this, "GeneralInternalServerErrorHandler: {}, Message: {}", cause.getClass().getSimpleName(),
				ex.getMessage());
		return getResponseEntity(cause, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * Handler for all spring exceptions that need to be logged
	 *
	 * @param ex
	 * @return corresponding response entity with APIError format.
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ MissingRequestHeaderException.class, MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> springInternalServerErrorHandler(Exception ex, HttpServletRequest request) {
		Throwable cause = ex.getCause();
		if (cause == null) {
			error(this, "SpringInternalServerErrorHandler: {}, Message: {}", ex.getClass().getSimpleName(),
					ex.getMessage());
			return getResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
		}
		error(this, "SpringInternalServerErrorHandler: {}, Message: {}", cause.getClass().getSimpleName(),
				ex.getMessage());
		return getResponseEntity(cause, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * 
	 * @param ex
	 * @param status
	 * @param webRequest
	 * @return
	 */
	protected ResponseEntity<Object> getResponseEntity(Throwable ex, HttpStatus status, Object webRequest) {
		StackTraceElement[] exceptions = ex.getStackTrace();

		String errorMessage = null;
		for (StackTraceElement s : exceptions) {
			if (s.getLineNumber() >= 0) {
				String message = ex.getLocalizedMessage();
				errorMessage = (message == null) ? " There is no message available" : message;
				break;
			}
		}

		Map<String, Object> additionalInfo = populateAdditionalInfo(webRequest);
		ApiError apiError = populateApiError(ex, status, errorMessage, additionalInfo);

		switch (status) {
		case OK:
			return this.ok(apiError);
		case NOT_FOUND:
			return this.notFound(apiError);
		case BAD_REQUEST:
			return this.badRequest(apiError);
		case INTERNAL_SERVER_ERROR:
			return this.errorFound(apiError);
		case GATEWAY_TIMEOUT:
			return this.timeOut(apiError);
		// $CASES-OMITTED$
		default:
			return this.errorFound(null);
		}

	}

	/**
	 * This method populates a map with additional info to be returned with
	 * response.
	 * 
	 * @param webRequest
	 * @return additionalInfo
	 */
	Map<String, Object> populateAdditionalInfo(Object webRequest) {
		Map<String, Object> additionalInfo = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		if (webRequest instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) webRequest;
			additionalInfo.put("requestHeaders", GenericUtility.getServletHeaders(request, headers));
			additionalInfo.put("requestMethod", request.getMethod());
			additionalInfo.put("requestPath", request.getRequestURI());
		} else if (webRequest instanceof WebRequest) {
			WebRequest request = (WebRequest) webRequest;
			additionalInfo.put("requestHeaders", GenericUtility.getWebHeaders(request, headers));
		}
		return additionalInfo;
	}

	/**
	 * This method populates the Api error object to be returned to be returned in
	 * response.
	 * 
	 * @param ex
	 * @param status
	 * @param errorMessage
	 * @param additionalInfo
	 * @return apiError
	 */
	ApiError populateApiError(Throwable ex, HttpStatus status, String debugMessage,
			Map<String, Object> additionalInfo) {
		String exceptionName = ex.getClass().getSimpleName();
		ApiError apiError;
		if (ex instanceof CustomException) {
			apiError = ((CustomException) ex).getEnclosedError();
			if (apiError == null) {
				apiError = new ApiError(new Date(), status.getReasonPhrase(), status.value(), exceptionName,
						debugMessage, null, additionalInfo);
			} else {
				if (apiError.getAdditionalInfo() != null) {
					apiError.getAdditionalInfo().putAll(additionalInfo);
				} else {
					apiError.setAdditionalInfo(additionalInfo);
				}
			}
		} else {
			apiError = new ApiError(new Date(), status.getReasonPhrase(), status.value(), exceptionName, debugMessage,
					null, additionalInfo);
		}
		return apiError;
	}

}
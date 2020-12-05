package com.oblivion.watchdogs.common.utility;

import static com.oblivion.watchdogs.common.logger.Log.defaultError;

import java.util.ArrayList;
import java.util.List;

import static com.oblivion.watchdogs.common.constants.GeneralConstants.D1;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.COMMON_ROOT;
import static com.oblivion.watchdogs.common.logger.Log.getStackTraceLimit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oblivion.watchdogs.common.logger.ExceptionLog;
import com.oblivion.watchdogs.common.logger.JSONExceptionLogger;

/**
 * @author Samuel D. A class used for general operations
 */
public abstract class GenericUtility {

	/**
	 * Used to convert objects to JSON string
	 */
	private static final Gson GSON = new GsonBuilder().create();

	/**
	 * Convert objects to JSON strings
	 *
	 * @param objects
	 * @throws JsonProcessingException
	 */
	public static void convertToJSON(Object... objects) {
		try {
			int size = objects.length;
			for (int i = 0; i < size; i++) {
				objects[i] = GSON.toJson(objects[i]);
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to convert object to JSON: {}", e);
		}
	}

	/**
	 * Gets the fully qualified domain name of an object
	 *
	 * @param o
	 * @return String
	 */
	public static String getClassFQDM(Object o) {
		String objectFQDM = "";
		try {
			if (o != null) {
				objectFQDM = o.getClass().getName().concat(D1);
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class,
					"An error occurred while trying to get an objects fully qualified domain name: {}", e);
		}
		return objectFQDM;
	}

	/**
	 * Gets a complete JSONExceptionLogger object
	 *
	 * @return String
	 * @throws JsonProcessingException
	 */
	public static String getJSONExceptionLogger(String methodSignature, Exception ex) {
		return getJSONByType(methodSignature, ex);
	}

	private static String getJSONByType(String methodSignature, Exception exception) {
		String jSONExceptionLoggerString = null;
		try {
			JSONExceptionLogger jSONExceptionLogger = new JSONExceptionLogger();
			jSONExceptionLogger.setConcludingMethodSignature(methodSignature);
			setExceptionAndInfo(exception, jSONExceptionLogger);
			List<Throwable> exceptions = getExceptions(exception);
			setLoggerFields(exceptions, jSONExceptionLogger);
			jSONExceptionLoggerString = getJSONExceptionLoggerString(jSONExceptionLogger).toString();
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to get a new jSONExceptionLogger: {}",
					e.getClass().getSimpleName());
		}
		return jSONExceptionLoggerString;
	}

	/**
	 * @param exception
	 * @return
	 */
	private static List<Throwable> getExceptions(Throwable exception) {
		List<Throwable> exceptions = new ArrayList<>();
		exceptions.add(exception);
		while (exception.getCause() != null) {
			exception = exception.getCause();
			exceptions.add(exception);
		}
		return exceptions;
	}

	/**
	 * This method sets the root exception message and any additional info.
	 *
	 * @param exception
	 * @param jsonExceptionLogger
	 */
	public static void setExceptionAndInfo(Throwable exception, JSONExceptionLogger jsonExceptionLogger) {
		try {
			jsonExceptionLogger.setRootExceptionMessage(exception.getLocalizedMessage());
		} catch (Exception e) {
			defaultError(GenericUtility.class,
					"An error occurred while trying to set JSONExceptionLogger exception and " + "additional "
							+ "info: {}",
					e.getClass().getSimpleName());
		}
	}

	/**
	 * This method determines if the exception is external(framework related) or
	 * internal(application related) and sets specific fields
	 *
	 * @param exception
	 * @param jsonExceptionLogger
	 */
	public static void setLoggerFields(List<Throwable> exceptions, JSONExceptionLogger jsonExceptionLogger) {
		jsonExceptionLogger.setExceptionLogs(new ArrayList<>());
		for (Throwable throwable : exceptions) {
			ExceptionLog exceptionLog = new ExceptionLog();
			String exceptionName = throwable.getClass().getName();
			Throwable cause = throwable.getCause();
			exceptionLog
					.setException(exceptionName + (cause != null ? ", Caused by: " + cause.getClass().getName() : ""));
			try {
				boolean isExternalException = true;
				StackTraceElement[] stackTraceElements = throwable.getStackTrace();
				int length = stackTraceElements.length;
				List<String> originatingIDELocators = new ArrayList<>();
				int element = 0;
				for (int i = 0; i < length; i++) {
					StackTraceElement stackTraceElement = stackTraceElements[i];
					String fileName = stackTraceElement.getClassName();
					int lineNumber = stackTraceElement.getLineNumber();
					if ((fileName.contains(COMMON_ROOT)) && lineNumber >= 0) {
						originatingIDELocators.add(stackTraceElement.toString());
						element++;
						String nextClassName = stackTraceElements[i + 1].getClassName();
						if (i + 1 == length || !nextClassName.contains(COMMON_ROOT) || nextClassName.contains("CGLIB")
								|| element >= getStackTraceLimit()) {
							isExternalException = false;
							break;
						}
					}
				}
				if (isExternalException) {
					if (length > getStackTraceLimit()) {
						length = getStackTraceLimit();
					}
					for (int i = 0; i < length; i++) {
						originatingIDELocators.add(stackTraceElements[i].toString());
					}
				}
				exceptionLog.setOriginatingIDELocators(originatingIDELocators);
			} catch (Exception e) {
				defaultError(GenericUtility.class,
						"An error occurred while trying to set JSONExceptionLogger fields: {}",
						e.getClass().getSimpleName());
			}
			jsonExceptionLogger.getExceptionLogs().add(exceptionLog);
		}
	}

	/**
	 * This method uses GSON to pretty print, and the exception jsonExceptionLogger
	 * as a string
	 *
	 * @param jsonExceptionLogger
	 * @return jelString
	 * @throws JsonProcessingException
	 */
	public static StringBuilder getJSONExceptionLoggerString(JSONExceptionLogger jsonExceptionLogger) {
		StringBuilder jelString = new StringBuilder();
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String gsonString = gson.toJson(jsonExceptionLogger);
			jelString = new StringBuilder(gsonString.replaceAll("\"", ""));
		} catch (Exception e) {
			defaultError(GenericUtility.class,
					"An error occurred while trying to transform JSONExceptionLogger as String: {}",
					e.getClass().getSimpleName());
		}
		return jelString;
	}

}
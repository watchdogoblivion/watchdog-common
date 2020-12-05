package com.oblivion.watchdogs.common.utility;

import static com.oblivion.watchdogs.common.constants.GeneralConstants.D1;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.SERVICE;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.COMMON_ROOT;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.root;
import static com.oblivion.watchdogs.common.logger.Log.defaultError;
import static com.oblivion.watchdogs.common.logger.Log.defaultTrace;
import static com.oblivion.watchdogs.common.logger.Log.getStackTraceLimit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oblivion.watchdogs.common.exceptions.CustomException;
import com.oblivion.watchdogs.common.logger.ExceptionLog;
import com.oblivion.watchdogs.common.logger.JSONExceptionLogger;
import com.oblivion.watchdogs.common.model.ApiError;
import com.oblivion.watchdogs.common.model.InMemoryTransaction;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Samuel D. A class used for general operations
 */
@Slf4j
public abstract class GenericUtility {

	/**
	 * Used to convert objects to JSON string
	 */
	private static final Gson GSON = new GsonBuilder().create();

	/**
	 * 
	 * @return
	 */
	public static Properties getProperties() {
		Properties prop = new Properties();
		String propFileName = "application.properties";
		String ymlFileName = "application.yml";
		InputStream inputStream = null;
		try {
			inputStream = GenericUtility.class.getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				log.info("Unable to locate properties file. Attempting to search for a yml file.");
				inputStream = GenericUtility.class.getClass().getClassLoader().getResourceAsStream(ymlFileName);
				if (inputStream != null) {
					prop.load(inputStream);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}
			}
		} catch (Exception e) {
			log.error("An exception occurred during construction of Log.java: {}", Arrays.toString(e.getStackTrace()));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("An exception occurred during construction of Log.java: {}",
							Arrays.toString(e.getStackTrace()));
				}
			}
		}
		return prop;
	}

	/**
	 * This method adds headers from a WebRequest to a header map, to be returned in
	 * response.
	 *
	 * @param headers
	 * @param request
	 * @return headers
	 */
	public static Map<String, String> getWebHeaders(WebRequest request, Map<String, String> headers) {
		try {
			Iterator<String> headerIterator = request.getHeaderNames();
			while (headerIterator.hasNext()) {
				Object headerObject = headerIterator.next();
				String header = headerObject != null ? headerObject.toString() : null;
				if (header != null) {
					headers.put(header, request.getHeader(header));
				}
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to retrieve the web headers: {}",
					getJSONExceptionLogger("getWebHeaders", e));
		}
		return headers;
	}

	/**
	 * This method adds headers from a HttpServletRequest to a header map, to be
	 * returned in response.
	 *
	 * @param request
	 * @param headers
	 * @return headers
	 */
	public static Map<String, String> getServletHeaders(HttpServletRequest request, Map<String, String> headers) {
		try {
			Enumeration<String> headerEnumeration = request.getHeaderNames();
			if (headerEnumeration != null) {
				while (headerEnumeration.hasMoreElements()) {
					Object headerObject = headerEnumeration.nextElement();
					String header = headerObject != null ? headerObject.toString() : null;
					if (header != null) {
						headers.put(header, request.getHeader(header));
					}
				}
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to retrieved the servlet headers: {}",
					getJSONExceptionLogger("getServletHeaders", e));
		}
		return headers;
	}

	/**
	 * This method is used to truncate a string by an indicated limit
	 *
	 * @param limit
	 * @param str
	 * @return
	 */
	public static String truncateString(int limit, String str) {
		try {
			if (str.length() > limit) {
				return str.substring(0, limit).concat("...truncated");
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to truncate the string: {}",
					getJSONExceptionLogger("truncateString", e));
		}
		return str;
	}

	/**
	 * This method is used to merge an array and varargs
	 *
	 * @param objectsArray
	 * @param objects
	 * @return
	 */
	public static Object[] combineArray(Object[] objectsArray, Object... objects) {
		Object[] newArray = null;
		try {
			newArray = new Object[objectsArray.length + objects.length];

			int length = newArray.length;
			int objectsArrayLength = objectsArray.length;

			for (int i = 0; i < length; i++) {
				if (i < objectsArrayLength) {
					newArray[i] = objectsArray[i];
				} else {
					newArray[i] = objects[i - objectsArrayLength];
				}
			}
		} catch (Exception e) {
			defaultError(GenericUtility.class, "An error occurred while trying to combine the Arrays: {}",
					getJSONExceptionLogger("combineArray", e));
		}
		return newArray;
	}

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
			defaultError(GenericUtility.class, "An error occurred while trying to convert object to JSON: {}",
					getJSONExceptionLogger("convertToJSON", e));
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
					"An error occurred while trying to get an objects fully qualified domain name: {}",
					getJSONExceptionLogger("getClassFQDM", e));
		}
		return objectFQDM;
	}

	public static String getMethodFQDN(Object object, String methodName) {
		return object.getClass().getName().concat(".").concat(methodName);
	}

	/**
	 * Gets a complete JSONExceptionLogger object using a join point
	 *
	 * @param jp
	 * @param ex
	 * @return String
	 * @throws JsonProcessingException
	 */
	public static String getJSONExceptionLogger(JoinPoint jp, Exception ex) {
		return getJSONByType(jp, ex);
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

	private static String getJSONByType(Object methodSignature, Exception exception) {
		String jSONExceptionLoggerString = null;
		try {
			JSONExceptionLogger jSONExceptionLogger = new JSONExceptionLogger();
			if (methodSignature instanceof JoinPoint) {
				jSONExceptionLogger
						.setConcludingMethodSignature(((JoinPoint) methodSignature).getSignature().toString());
			} else if (methodSignature instanceof String) {
				jSONExceptionLogger.setConcludingMethodSignature((String) methodSignature);
			}
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
			if (exception instanceof CustomException && ((CustomException) exception).getEnclosedError() != null) {
				CustomException customException = (CustomException) exception;
				ApiError apiError = customException.getEnclosedError();
				if (apiError != null) {
					jsonExceptionLogger.setRootExceptionMessage(apiError.getDebugMessage());
					jsonExceptionLogger.setAdditionalInfo(apiError.getAdditionalInfo());
				}
			} else {
				jsonExceptionLogger.setRootExceptionMessage(exception.getLocalizedMessage());
			}
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
					if ((root != null && fileName.contains(root) || fileName.contains(COMMON_ROOT))
							&& lineNumber >= 0) {
						originatingIDELocators.add(stackTraceElement.toString());
						element++;
						String nextClassName = stackTraceElements[i + 1].getClassName();
						if (i + 1 == length || !nextClassName.contains(root) || nextClassName.contains("CGLIB")
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

	/**
	 * Used to get the context info for a request
	 *
	 * @return contextInfo
	 */
	public static String getContextInfo() {
		StringBuilder contextInfo = new StringBuilder();
		final String errorMessage = "An error occurred while trying to get the user info from the "
				+ "RequestContextHolder: {}";
		try {
			RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
			if (requestAttributes instanceof InMemoryTransaction) {
				InMemoryTransaction inMemoryTransaction = (InMemoryTransaction) requestAttributes;
				contextInfo.append("[").append(SERVICE).append(": ").append(inMemoryTransaction.getService())
						.append("]");
			} else {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
						.currentRequestAttributes()).getRequest();
				contextInfo.append("[").append(SERVICE).append(": ").append(request.getServletPath()).append("]");

			}
		} catch (IllegalStateException i) {
			defaultTrace(GenericUtility.class, errorMessage, getJSONExceptionLogger("getUserInfo", i));
		} catch (Exception e) {
			defaultError(GenericUtility.class, errorMessage, getJSONExceptionLogger("getUserInfo", e));
		}
		return contextInfo.toString();
	}

}
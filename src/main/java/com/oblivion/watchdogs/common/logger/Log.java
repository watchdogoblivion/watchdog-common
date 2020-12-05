package com.oblivion.watchdogs.common.logger;

import static com.oblivion.watchdogs.common.constants.GeneralConstants.ARG;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D1;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D2;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D3;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D4;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.ERROR_OUT;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.GLOBAL_CONSOLE_LOG_DATE_FORMAT;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.OUT;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.root;
import static com.oblivion.watchdogs.common.utility.GenericUtility.getClassFQDM;
import static com.oblivion.watchdogs.common.utility.GenericUtility.getJSONExceptionLogger;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.oblivion.watchdogs.common.utility.GenericUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Samuel D. logger class for all logging and Printing outputs
 */
@Slf4j
@Component
@DependsOn(value = { "GeneralConstants" })
public class Log {

	/**
	 * Used for instance method invocation inside static context
	 */
	private static Log logInstance = new Log();

	/**
	 * Error message for any errors during logging and printing
	 */
	private static String messageError = "An exception occurred while trying to log a message" + D1;

	/**
	 * Used to add date to system outputs
	 */
	private static DateFormat dateFormat = new SimpleDateFormat(GLOBAL_CONSOLE_LOG_DATE_FORMAT);

	/**
	 * Static field to hold logHeaderLimit after its been initialized
	 */
	public static int headerLimit;

	/**
	 * Static field to hold logPayloadLimit after its been initialized
	 */
	public static int payloadLimit;

	/**
	 * Static field to hold logStackTraceLimit after its been initialized
	 */
	public static int stackTraceLimit;

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static Log getLogInstance() {
		return logInstance;
	}

	/**
	 * Setter
	 * 
	 * @param logInstance
	 */
	public static void setLogInstance(Log logInstance) {
		Log.logInstance = logInstance;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static String getMessageError() {
		return messageError;
	}

	/**
	 * Setter
	 * 
	 * @param logInstance
	 */
	public static void setMessageError(String messageError) {
		Log.messageError = messageError;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * Setter
	 * 
	 * @param logInstance
	 */
	public static void setDateFormat(DateFormat dateFormat) {
		Log.dateFormat = dateFormat;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static int getHeaderLimit() {
		return headerLimit;
	}

	/**
	 * Setter
	 * 
	 * @param logInstance
	 */
	public static void setHeaderLimit(int headerLimit) {
		Log.headerLimit = headerLimit;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static int getPayloadLimit() {
		return payloadLimit;
	}

	/**
	 * Setter
	 * 
	 * @param logInstance
	 */
	public static void setPayloadLimit(int payloadLimit) {
		Log.payloadLimit = payloadLimit;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public static int getStackTraceLimit() {
		return stackTraceLimit;
	}

	public static void setStackTraceLimit(int stackTraceLimit) {
		Log.stackTraceLimit = stackTraceLimit;
	}

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	/**
	 * Size limit for logging individual headers
	 */
	@Value("${log.header.limit:30}")
	private int logHeaderLimit;

	/**
	 * Size limit for logging objects
	 */
	@Value("${log.payload.limit:5000}")
	private int logPayloadLimit;

	/**
	 * Size limit for log object
	 * com.cvs.opportunity.common.logger.JSONExceptionLogger
	 */
	@Value("${log.stackTrace.limit:10}")
	private int logStackTraceLimit;

	/**
	 * Used for error logging
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void error(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isErrorEnabled()) {
				logInstance.setMDC();
				log.error(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for error logging with object mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void errorToJSON(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isErrorEnabled()) {
				logInstance.setMDC();
				logInstance.convertToJSON(objects);
				log.error(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for error logging an exception after converting it to JSON using pretty
	 * print
	 *
	 * @param instance   The object whose method is logging the exception
	 * @param message    Any message for additional details
	 * @param methodName The method of the object which is logging the exception
	 * @param ex
	 */
	public static void prettyError(Object instance, String methodName, String message, Exception ex,
			Object... objects) {
		try {
			error(instance, message, objects, getJSONExceptionLogger(methodName, ex));
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for error logging an exception after converting it to JSON using pretty
	 * print
	 *
	 * @param instance   The object whose method is logging the throwable
	 * @param message    Any message for additional details
	 * @param methodName The method of the object which is logging the throwable
	 * @param ex
	 */
	public static void prettyError(Object instance, String message, String methodName, Throwable throwable,
			Object... objects) {
		try {
			prettyError(instance, methodName, message, new Exception(throwable), objects);
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Convenience method used for error logging using original slf4j with no
	 * changes
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void defaultError(Object instance, String message, Object... objects) {
		Logger log = LoggerFactory.getLogger(instance.getClass());
		if (log.isErrorEnabled()) {
			log.error(logInstance.checkMessage(message, objects), objects);
		}
	}

	/**
	 * Used for warning logging
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void warn(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isWarnEnabled()) {
				logInstance.setMDC();
				log.warn(logInstance.checkMessage(message), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for warning logging with object mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void warnToJSON(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isWarnEnabled()) {
				logInstance.setMDC();
				logInstance.convertToJSON(objects);
				log.warn(logInstance.checkMessage(message), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Convenience method used for warn logging using original slf4j with no changes
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void defaultWarn(Object instance, String message, Object... objects) {
		Logger log = LoggerFactory.getLogger(instance.getClass());
		if (log.isWarnEnabled()) {
			log.warn(logInstance.checkMessage(message), objects);
		}
	}

	/**
	 * Used for info logging
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void info(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isInfoEnabled()) {
				logInstance.setMDC();
				log.info(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
			e.printStackTrace();
		}
	}

	/**
	 * Used for info logging with object mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void infoToJSON(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isInfoEnabled()) {
				logInstance.setMDC();
				logInstance.convertToJSON(objects);
				log.info(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Convenience method used for info logging using original slf4j with no changes
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void defaultInfo(Object instance, String message, Object... objects) {
		Logger log = LoggerFactory.getLogger(instance.getClass());
		if (log.isInfoEnabled()) {
			log.info(logInstance.checkMessage(message, objects), objects);
		}
	}

	/**
	 * Used for debug logging
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void debug(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isDebugEnabled()) {
				logInstance.setMDC();
				log.debug(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for debug logging with object mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void debugToJSON(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isDebugEnabled()) {
				logInstance.setMDC();
				logInstance.convertToJSON(objects);
				log.debug(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Convenience method used for debug logging using original slf4j with no
	 * changes
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void defaultDebug(Object instance, String message, Object... objects) {
		Logger log = LoggerFactory.getLogger(instance.getClass());
		if (log.isDebugEnabled()) {
			log.debug(logInstance.checkMessage(message, objects), objects);
		}
	}

	/**
	 * Used for trace logging
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void trace(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isTraceEnabled()) {
				logInstance.setMDC();
				log.trace(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Used for trace logging with object mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void traceToJSON(Object instance, String message, Object... objects) {
		try {
			Logger log = LoggerFactory.getLogger(instance.getClass());
			if (log.isTraceEnabled()) {
				logInstance.setMDC();
				logInstance.convertToJSON(objects);
				log.trace(logInstance.checkMessage(message, objects), objects);
			}
		} catch (Exception e) {
			logStandardError(e);
		}
	}

	/**
	 * Convenience method used for trace logging using original slf4j with no
	 * changes
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void defaultTrace(Object instance, String message, Object... objects) {
		Logger log = LoggerFactory.getLogger(instance.getClass());
		if (log.isTraceEnabled()) {
			log.trace(logInstance.checkMessage(message, objects), objects);
		}
	}

	/**
	 * Uses system console to print standard outputs separated by line feed
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void sysOut(Object instance, String message, Object... objects) {
		try {
			String threadName = "[" + Thread.currentThread().getName() + "]";
			System.out.println(dateFormat.format(new Date()) + D3 + OUT + D3 + threadName + D3 + getClassFQDM(instance)
					+ logInstance.getContextInfo() + D3 + logInstance.getFormattedMessage(message, objects));
		} catch (Exception e) {
			printStandardError(e);
		}
	}

	/**
	 * Uses system console to print standard outputs separated by line feed with
	 * GSON mapper.
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void sysOutToJSON(Object instance, String message, Object... objects) {
		try {
			String threadName = "[" + Thread.currentThread().getName() + "]";
			logInstance.convertToJSON(objects);
			System.out.println(dateFormat.format(new Date()) + D3 + OUT + D3 + threadName + D3 + getClassFQDM(instance)
					+ logInstance.getContextInfo() + D3 + logInstance.getFormattedMessage(message, objects));
		} catch (Exception e) {
			printStandardError(e);
		}
	}

	/**
	 * Uses system console to print error outputs separated by line feed
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void sysErr(Object instance, String message, Object... objects) {
		try {
			String threadName = "[" + Thread.currentThread().getName() + "]";
			System.err.println(
					dateFormat.format(new Date()) + D3 + ERROR_OUT + D3 + threadName + D3 + getClassFQDM(instance)
							+ logInstance.getContextInfo() + D3 + logInstance.getFormattedMessage(message, objects));
		} catch (Exception e) {
			printStandardError(e);
		}
	}

	/**
	 * Uses system console to print error outputs separated by line feed, with GSON
	 * mapper
	 *
	 * @param instance
	 * @param message
	 * @param objects
	 */
	public static void sysErrToJSON(Object instance, String message, Object... objects) {
		try {
			String threadName = "[" + Thread.currentThread().getName() + "]";
			logInstance.convertToJSON(objects);
			System.err.println(
					dateFormat.format(new Date()) + D3 + ERROR_OUT + D3 + threadName + D3 + getClassFQDM(instance)
							+ logInstance.getContextInfo() + D3 + logInstance.getFormattedMessage(message, objects));
		} catch (Exception e) {
			printStandardError(e);
		}
	}

	/**
	 * Gets the final string to print by formatting
	 *
	 * @param message
	 * @param objects
	 * @return
	 */
	public String getFormattedMessage(String message, Object... objects) {
		String formattedMessage = logInstance.checkMessage(message, objects).replace("{}", "%s");
		formattedMessage = String.format(formattedMessage, (objects));
		return formattedMessage;
	}

	/**
	 * Centralized location for the standard logging of any errors that occur trying
	 * to log.
	 *
	 * @param e
	 */
	protected static void logStandardError(Exception e) {
		log.error(messageError + e.getClass().getSimpleName() + D4 + e.getLocalizedMessage());
	}

	/**
	 * Centralized location for the standard printing of any errors that occur
	 * trying to log.
	 *
	 * @param e
	 */
	protected static void printStandardError(Exception e) {
		System.err.println(dateFormat.format(new Date()) + D3 + ERROR_OUT + D3 + e.getClass().getSimpleName() + D4
				+ messageError + e.getLocalizedMessage());
	}

	/**
	 * Re-implementing since method is not inherited
	 *
	 * @return boolean
	 */
	public static boolean isErrorEnabled(Object instance) {
		return LoggerFactory.getLogger(instance.getClass()).isErrorEnabled();
	}

	/**
	 * Re-implementing since method is not inherited
	 *
	 * @return boolean
	 */
	public static boolean isWarnEnabled(Object instance) {
		return LoggerFactory.getLogger(instance.getClass()).isWarnEnabled();
	}

	/**
	 * Re-implementing since method is not inherited
	 *
	 * @return boolean
	 */
	public static boolean isInfoEnabled(Object instance) {
		return LoggerFactory.getLogger(instance.getClass()).isInfoEnabled();
	}

	/**
	 * Re-implementing since method is not inherited
	 *
	 * @return boolean
	 */
	public static boolean isDebugEnabled(Object instance) {
		return LoggerFactory.getLogger(instance.getClass()).isDebugEnabled();
	}

	/**
	 * Re-implementing since method is not inherited
	 *
	 * @return
	 */
	public static boolean isTraceEnabled(Object instance) {
		return LoggerFactory.getLogger(instance.getClass()).isTraceEnabled();
	}

	/**
	 * @param message
	 * @return
	 */
	public String checkMessage(String message, Object... objects) {
		if (message == null || message.isEmpty()) {
			StringBuilder args = new StringBuilder("Objects: ");
			int length = 0;
			if (null != objects) {
				length = objects.length;
			}
			if (length == 0) {
				return args + " No objects provided ";
			}
			for (int i = 1; i <= length; i++) {
				args.append(ARG + D2);
			}
			args.delete(args.lastIndexOf(D2), args.length());
			return args.toString();
		}
		return message;
	}

	protected void setMDC() {
		MDC.put("contextInfo", logInstance.getContextInfo());
	}

	/**
	 * Method should be overridden to apply custom context info to logs
	 * 
	 * @return
	 */
	protected String getContextInfo() {
		return "[WatchDogOblivion]";
	}

	/**
	 * Used to convert objects to JSON strings with GSON library. Should override if
	 * different library is desired for conversion.
	 *
	 * @param objects
	 * @throws JsonProcessingException
	 */
	protected void convertToJSON(Object... objects) {
		GenericUtility.convertToJSON(objects);
	}

	/**
	 * This method sets the auto wired field values, to corresponding static fields
	 * so that this class can act statically.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	@PostConstruct
	private void postConstruct() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		headerLimit = this.logHeaderLimit;
		payloadLimit = this.logPayloadLimit;
		stackTraceLimit = this.logStackTraceLimit;
		log.info("Header limit: {}, Payload limit: {}, Stacktrace limit:{}", headerLimit, payloadLimit,
				stackTraceLimit);
		if (root != null && !root.isEmpty()) {
			Reflections reflections = new Reflections(root);
			Set<Class<? extends Log>> subTypes = reflections.getSubTypesOf(Log.class);
			int size = subTypes.size();
			if (size != 1) {
				if (size > 1) {
					log.warn(
							"Multiple Log subclasses were found on the classpath. Using default Log class instance instead of subclasses");
				} else {
					log.info("No Log subclasses were found, using default Log class instance");
				}
			} else {
				Class<?> classType = subTypes.toArray(new Class[1])[0];
				log.info("Located Log subclass {}, using it for Instantiation", classType.getName());
				logInstance = (Log) classType.getDeclaredConstructor().newInstance();
			}
		} else {
			log.info("Root property is null, using default Log class instance");
		}
	}

}
package com.oblivion.watchdogs.common.logger;

import static com.oblivion.watchdogs.common.constants.GeneralConstants.ARG;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D1;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D2;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.D4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oblivion.watchdogs.common.utility.GenericUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Samuel D. logger class for all logging and Printing outputs
 */
@Slf4j
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
			error(instance, message, objects, ex);
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
	 * Centralized location for the standard logging of any errors that occur trying
	 * to log.
	 *
	 * @param e
	 */
	protected static void logStandardError(Exception e) {
		log.error(messageError + e.getClass().getSimpleName() + D4 + e.getLocalizedMessage());
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

}
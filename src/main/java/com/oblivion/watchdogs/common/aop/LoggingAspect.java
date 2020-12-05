package com.oblivion.watchdogs.common.aop;

import static com.oblivion.watchdogs.common.constants.GeneralConstants.D1;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.END_METHOD;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.EXECUTION_MS;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.JAVA_LANG;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.JAVA_UTIL;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.METHOD_EXCEPTION;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.START_METHOD;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.VOID;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.WITH_ARGS;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.WITH_RESPONSE;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.COMMON_ROOT;
import static com.oblivion.watchdogs.common.constants.GeneralConstants.root;
import static com.oblivion.watchdogs.common.logger.Log.debug;
import static com.oblivion.watchdogs.common.logger.Log.getPayloadLimit;
import static com.oblivion.watchdogs.common.logger.Log.info;
import static com.oblivion.watchdogs.common.logger.Log.isDebugEnabled;
import static com.oblivion.watchdogs.common.logger.Log.isInfoEnabled;
import static com.oblivion.watchdogs.common.logger.Log.prettyError;
import static com.oblivion.watchdogs.common.utility.GenericUtility.truncateString;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oblivion.watchdogs.common.annotations.LogLevel;
import com.oblivion.watchdogs.common.annotations.LoggingAspectEnabled;
import com.oblivion.watchdogs.common.logger.Log;

import lombok.Data;

/**
 * @author Samuel Aspect to address the cross cutting concern: Logging
 */
@Data
@Aspect
@Component
public abstract class LoggingAspect {

	/**
	 * Message for logging the end of a method with response
	 */
	private String endMethodMessageResponse = END_METHOD + D1 + WITH_RESPONSE + D1 + EXECUTION_MS;

	/**
	 * Message for logging the end of a method
	 */
	private String endMethodMessage = END_METHOD + D1 + EXECUTION_MS;

	/**
	 * Message for logging the start of a method
	 */
	private String startMethodMessageArgs = START_METHOD + D1 + WITH_ARGS;

	/**
	 * Used to convert objects to JSON
	 */
	private Gson gson = new GsonBuilder().create();

	/**
	 * Exception message
	 */
	private String exceptionMessage = END_METHOD + D1 + METHOD_EXCEPTION + D1 + EXECUTION_MS;

	/**
	 * Centralized point cut for specific advice methods
	 */
	@Pointcut("within(*..controllers.*)")
	protected void controllerPackage() {
		// No implementation necessary
	}

	/**
	 * Centralized point cut for specific advice methods - Applies for types that
	 * have the specified annotation type
	 */
	@Pointcut("@within(com.oblivion.watchdogs.common.annotations.LoggingAspectEnabled)")
	protected void loggingAspectType() {
		// No implementation necessary
	}

	/**
	 * Centralized point cut for specific advice methods - Applies for methods with
	 * the specified annotation
	 */
	@Pointcut("@annotation(com.oblivion.watchdogs.common.annotations.LoggingAspectEnabled)")
	protected void loggingAspectAnnotation() {
		// No implementation necessary
	}

	/**
	 * This method is used to log the around a method. By default it will use the
	 * log level of the package or class. For Debug it will log args, any lower it
	 * will not. If you specify a value for the annotation, it will log at that
	 * level strictly. The args value for the annotation will attach arguments
	 * regardless of level if set to true. Useful for method level differentiation.
	 *
	 * @param jp Object containing all the information pertaining to the method
	 *           where the advice was applied.
	 */
	@Around("loggingAspectType() || loggingAspectAnnotation()")
	protected Object logAround(ProceedingJoinPoint jp) throws Throwable {
		Object response;
		List<Object> filteredArgs = getFilteredArgs(jp.getArgs());
		Object instance = jp.getTarget();
		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();

		LoggingAspectEnabled loggingAspectEnabled;
		if (method.isAnnotationPresent(LoggingAspectEnabled.class)) {
			loggingAspectEnabled = method.getAnnotation(LoggingAspectEnabled.class);
		} else {
			loggingAspectEnabled = instance.getClass().getAnnotation(LoggingAspectEnabled.class);
		}
		LogLevel baseLevel = loggingAspectEnabled.baseLevel();

		if (baseLevel.getLevel() != Level.OFF) {
			LogLevel argsLevel = loggingAspectEnabled.argsLevel();
			LogLevel responseLevel = loggingAspectEnabled.responseLevel();
			response = logAroundLevel(jp, filteredArgs, instance, signature, baseLevel, argsLevel, responseLevel);
		} else {
			response = logAroundDefault(jp, filteredArgs, instance, signature);
		}
		return response;
	}

	/**
	 * Logs around a message with only info level
	 *
	 * @param jp
	 * @param filteredArgs
	 * @param start
	 * @param instance
	 * @param signature
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	protected Object logAroundLevel(ProceedingJoinPoint jp, List<Object> filteredArgs, Object instance,
			MethodSignature signature, LogLevel baseLevel, LogLevel argsLevel, LogLevel responseLevel)
			throws Throwable {
		Method baseMethod = getLogMethod(baseLevel);

		Object response;
		if (argsLevel.getLevel() != Level.OFF) {
			if ((boolean) isLogMethodEnabled(argsLevel).invoke(Log.class, instance)) {
				Method argsMethod = getLogMethod(argsLevel);
				argsMethod.invoke(Log.class, instance, startMethodMessageArgs,
						new Object[] { signature, getMethodArgs(filteredArgs) });
			} else {
				baseMethod.invoke(Log.class, instance, START_METHOD, new Object[] { jp.getSignature() });
			}
		} else {
			baseMethod.invoke(Log.class, instance, START_METHOD, new Object[] { jp.getSignature() });
		}

		long start = System.currentTimeMillis();
		try {
			response = jp.proceed();
		} catch (Throwable e) {
			baseMethod.invoke(Log.class, instance, exceptionMessage,
					new Object[] { signature, System.currentTimeMillis() - start });
			throw e;
		}
		long end = System.currentTimeMillis() - start;

		if (responseLevel.getLevel() != Level.OFF) {
			if ((boolean) isLogMethodEnabled(responseLevel).invoke(Log.class, instance)) {
				Method responseMethod = getLogMethod(responseLevel);
				responseMethod.invoke(Log.class, instance, endMethodMessageResponse,
						new Object[] { signature, getMethodResponse(response), end });
			} else {
				baseMethod.invoke(Log.class, instance, endMethodMessage, new Object[] { signature, end });
			}
		} else {
			baseMethod.invoke(Log.class, instance, endMethodMessage, new Object[] { signature, end });
		}
		return response;
	}

	/**
	 * Logs around a method with default log levels
	 *
	 * @param jp
	 * @param filteredArgs
	 * @param start
	 * @param instance
	 * @param signature
	 * @return
	 * @throws Throwable
	 */
	protected Object logAroundDefault(ProceedingJoinPoint jp, List<Object> filteredArgs, Object instance,
			MethodSignature signature) throws Throwable {
		Object response;
		if (isDebugEnabled(instance)) {
			debug(instance, startMethodMessageArgs, signature, getMethodArgs(filteredArgs));
		} else if (isInfoEnabled(instance)) {
			info(instance, START_METHOD, jp.getSignature());
		}

		long start = System.currentTimeMillis();
		try {
			response = jp.proceed();
		} catch (Throwable e) {
			info(instance, exceptionMessage, signature, System.currentTimeMillis() - start);
			throw e;
		}
		long end = System.currentTimeMillis() - start;

		if (isDebugEnabled(instance)) {
			debug(instance, endMethodMessageResponse, signature, getMethodResponse(response), end);
		} else if (isInfoEnabled(instance)) {
			info(instance, endMethodMessage, signature, end);
		}
		return response;
	}

	/**
	 * Gets the log method from com.cvs.opportunity.common.logger based on log level
	 *
	 * @param level
	 * @return
	 * @throws NoSuchMethodException
	 */
	protected Method getLogMethod(LogLevel level) throws NoSuchMethodException {
		String levelMethodName = level.name().toLowerCase();
		return Log.class.getMethod(levelMethodName, Object.class, String.class, Object[].class);
	}

	/**
	 * Checks if log level is enabled
	 *
	 * @param level
	 * @return
	 */
	protected Method isLogMethodEnabled(LogLevel level) throws NoSuchMethodException {
		String levelName = level.name();
		String methodPartialName = levelName.charAt(0) + levelName.substring(1).toLowerCase();
		String methodName = "is" + methodPartialName + "Enabled";
		return Log.class.getMethod(methodName, Object.class);
	}

	/**
	 * Get the string representation of a methods arguments
	 *
	 * @param filteredArgs
	 * @return
	 */
	public String getMethodArgs(List<Object> filteredArgs) {
		return !filteredArgs.isEmpty() ? truncateString(getPayloadLimit(), gson.toJson(filteredArgs)) : VOID;
	}

	/**
	 * Get the string representation of a methods response
	 *
	 * @param response
	 * @return
	 */
	public String getMethodResponse(Object response) {
		return response != null ? truncateString(getPayloadLimit(), gson.toJson(response)) : VOID;
	}

	/**
	 * This method is used to add cvs objects to a list, and objects that cannot be
	 * mapped through ObjectMapper as simple class names for logging.
	 *
	 * @param args
	 * @return filteredArgs
	 */
	public List<Object> getFilteredArgs(Object[] args) {
		List<Object> filteredArgs = new LinkedList<>();
		Set<String> filters = getFilters();
		if (args != null && args.length != 0) {
			for (Object arg : args) {
				if (arg != null) {
					String className = arg.getClass().getName();
					boolean addFiltered = false;
					for (String filter : filters) {
						if (className.contains(filter)) {
							addFiltered = true;
							break;
						}
					}
					if (addFiltered) {
						filteredArgs.add(arg);
					} else {
						filteredArgs.add(arg.getClass().getSimpleName());
					}
				}

			}
		}
		return filteredArgs;
	}

	/**
	 * This method gets the packages to convert with an ObjectMapper, as some
	 * libraries will throw exception if converting during our code execution.
	 *
	 * @return
	 */
	public Set<String> getFilters() {
		Set<String> filters = new HashSet<>();
		if (root != null) {
			filters.add(root);
		}
		filters.add(COMMON_ROOT);
		filters.add(JAVA_UTIL);
		filters.add(JAVA_LANG);
		return filters;
	}

	/**
	 * This method is used to log exceptions that are not caught by a method at the
	 * specified pointcut.
	 *
	 * @param jp Object containing all the information pertaining to the point of
	 *           execution where the advice was applied.
	 * @param ex The exception thrown and not caught by a method.
	 */
	@AfterThrowing(pointcut = "controllerPackage()", throwing = "ex")
	protected void controllerExceptionLogging(JoinPoint jp, Exception ex) {
		String args = getMethodArgs(getFilteredArgs(jp.getArgs()));
		String message = "An exception occurred during a service transaction with reequest body: {}, {}";
		prettyError(jp.getTarget(), jp.getSignature().toString(), message, ex, args);
	}

}
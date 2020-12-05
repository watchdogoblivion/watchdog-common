package com.oblivion.watchdogs.common.annotations;

import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Samuel D. This is a custom annotation to exclude methods and types
 *         (classes, interfaces, and enums) from the logging aspect.
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface LoggingAspectDisabled {
}
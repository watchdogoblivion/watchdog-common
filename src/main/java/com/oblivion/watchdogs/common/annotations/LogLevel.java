package com.oblivion.watchdogs.common.annotations;

import org.apache.logging.log4j.Level;

/**
 * 
 * @author Samuel D.
 *
 */
public enum LogLevel {

	OFF(Level.OFF), ERROR(Level.ERROR), WARN(Level.WARN), INFO(Level.INFO), DEBUG(Level.DEBUG), TRACE(Level.TRACE);

	private Level level;

	LogLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

}
package com.oblivion.watchdogs.common.utility;

import static com.oblivion.watchdogs.common.logger.Log.defaultError;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

}
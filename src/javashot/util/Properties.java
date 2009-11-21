/**
 * Copyright (C) 2009 Samir Chekkal
 * This program is distributed under the terms of the GNU General Public License.
 */

/**
 * @author chekkal
 *
 */
package javashot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Contains static methods to simplify access to configuration parameters
 */

public class Properties {
	private static java.util.Properties properties = new java.util.Properties();
	static {
		try {
			properties.load(new FileInputStream(System.getenv("JAVASHOT_HOME") + "/javashot.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't load javashot.properties");
		}
	}

	/**
	 * Returns the root directory for JAVASHOT
	 */
	public static String getJavashotHome() {
		return System.getenv("JAVASHOT_HOME");
	}

	/**
	 * Returns the short name of the class at which the capture will start
	 */
	public static String getStartCaptureAt() {
		return (String) properties.get("startCaptureAt");
	}

	/**
	 * Returns an array of patterns for classes to be instrumented
	 */
	public static ArrayList<Pattern> getInstrumentationClassPattern() {
		String instrumentationClassPattern = (String) properties.get("instrumentationClassPattern");
		if (instrumentationClassPattern == null || instrumentationClassPattern.isEmpty()) {
			return new ArrayList<Pattern>();
		}
		ArrayList<Pattern> result = new ArrayList<Pattern>();
		StringTokenizer tokens = new StringTokenizer(instrumentationClassPattern, ",");
		while (tokens.hasMoreElements()) {
			result.add(Pattern.compile(tokens.nextToken().toLowerCase()));
		}
		return result;
	}
}

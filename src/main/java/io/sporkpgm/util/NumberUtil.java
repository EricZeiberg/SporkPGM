/*
 * Copyright 2013 Maxim Salikhov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sporkpgm.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class to assist with parsing strings into numbers
 *
 * @author msalihov (Maxim Salikhov)
 */
public class NumberUtil {

	private static char[] allowed;

	/**
	 * Parses a string into an integer omitting any letters or other characters
	 * in it
	 */
	public static Integer parseInteger(String s) {
		allowed = "-1234567890".toCharArray();
		StringBuilder allowedChars = new StringBuilder();
		boolean negative = s.startsWith("-");
		for(char c : s.toLowerCase().toCharArray()) {
			for(char i : allowed) {
				if(c == i) {
					allowedChars.append(String.valueOf(c));
				}
			}
		}
		try {
			if(negative) {
				return Integer.parseInt(allowedChars.toString()) * -1;
			} else {
				return Integer.parseInt(allowedChars.toString());
			}
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to integer!");
			return null;
		}
	}

	/**
	 * Parses a string into a double omitting any letters or other characters in
	 * it
	 */
	public static Double parseDouble(String s) {
		allowed = ".-1234567890".toCharArray();
		StringBuilder allowedChars = new StringBuilder();
		boolean negative = s.startsWith("-");
		for(char c : s.toLowerCase().toCharArray()) {
			for(char i : allowed) {
				if(c == i) {
					allowedChars.append(String.valueOf(c));
				}
			}
		}
		try {
			if(negative) {
				return Double.parseDouble(allowedChars.toString()) * -1;
			} else {
				return Double.parseDouble(allowedChars.toString());
			}
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to double!");
			return null;
		}
	}

	/**
	 * Parses a string into a float omitting any letters (excluding f) or other
	 * characters in it
	 */
	public static Float parseFloat(String s) {
		allowed = ".-1234567890f".toCharArray();
		StringBuilder allowedChars = new StringBuilder();
		boolean negative = s.startsWith("-");
		for(char c : s.toLowerCase().toCharArray()) {
			for(char i : allowed) {
				if(c == i) {
					allowedChars.append(String.valueOf(c));
				}
			}
		}
		try {
			if(negative) {
				return Float.parseFloat(allowedChars.toString()) * -1;
			} else {
				return Float.parseFloat(allowedChars.toString());
			}
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to float!");
			return null;
		}
	}

	/**
	 * Parses a string into a long omitting any letters (excluding l) or other
	 * characters in it
	 */
	public static Long parseLong(String s) {
		allowed = ".-1234567890l".toCharArray();
		StringBuilder allowedChars = new StringBuilder();
		boolean negative = s.startsWith("-");
		for(char c : s.toLowerCase().toCharArray()) {
			for(char i : allowed) {
				if(c == i) {
					allowedChars.append(String.valueOf(c));
				}
			}
		}
		try {
			if(negative) {
				return Long.parseLong(allowedChars.toString()) * -1;
			} else {
				return Long.parseLong(allowedChars.toString());
			}
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to float!");
			return null;
		}
	}

	public static int getRandom(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static boolean randomBoolean() {
		return new Random().nextBoolean();
	}

	public static double getLowest(List<Double> doubles) {
		double lowest = doubles.get(0);
		for(int i = 1; i < doubles.size(); i++) {
			double value = doubles.get(i);
			if(lowest > value) {
				lowest = value;
			}
		}
		return lowest;
	}

	public static List<Object> getLowest(Map<Object, Double> map) {
		Object[] array = map.keySet().toArray();

		List<Object> objects = Lists.newArrayList(new Object[]{array[0]});
		double lowest = map.get(objects.get(0));
		for(int i = 1; i < map.size(); i++) {
			Object key = array[i];
			double value = map.get(key);
			if(lowest > value) {
				objects = Lists.newArrayList(new Object[]{key});
				lowest = value;
			} else if(lowest == value) {
				objects.add(key);
			}
		}

		return objects;
	}

}

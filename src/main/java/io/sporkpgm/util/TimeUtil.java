package io.sporkpgm.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

	public static DateTime parseTime(DateTime date, String time) {
		Map<String, Long> times = new HashMap<>();
		times.put("y", 365L * 24 * 60 * 60);
		times.put("m", 31L * 24 * 60 * 60);
		times.put("w", 7L * 24 * 60 * 60);
		times.put("d", 24L * 60 * 60);
		times.put("h", 60L * 60);
		times.put("s", 60L);

		Matcher m = Pattern.compile("(\\d+)([A-Za-z]+)").matcher(time);
		while(m.find()) {
			String multiplier = m.group(1);
			String type = m.group(2);
			switch(type) {
				case "y":
					date.plusYears(Integer.parseInt(multiplier));
					break;
				case "m":
					date.plusMonths(Integer.parseInt(multiplier));
					break;
				case "w":
					date.plusWeeks(Integer.parseInt(multiplier));
					break;
				case "d":
					date.plusDays(Integer.parseInt(multiplier));
					break;
				case "h":
					date.plusHours(Integer.parseInt(multiplier));
					break;
				case "s":
					date.plusSeconds(Integer.parseInt(multiplier));
					break;
			}
		}

		return date;
	}

	public static String timeAgoInWords(Period period) {
		PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
		if(period.getYears() != 0) {
			builder.appendYears().appendSuffix(" years ago\n");
		} else if(period.getMonths() != 0) {
			builder.appendMonths().appendSuffix(" months ago\n");
		} else if(period.getDays() != 0) {
			builder.appendDays().appendSuffix(" days ago\n");
		} else if(period.getHours() != 0) {
			builder.appendHours().appendSuffix(" hours ago\n");
		} else if(period.getMinutes() != 0) {
			builder.appendMinutes().appendSuffix(" minutes ago\n");
		} else if(period.getSeconds() != 0) {
			builder.appendSeconds().appendSuffix(" seconds ago\n");
		}
		PeriodFormatter formatter = builder.printZeroNever().toFormatter();
		return formatter.print(period);
	}

	public static String timeAgoInWords(DateTime time) {
		return timeAgoInWords(new Period(time, new DateTime()));
	}

	public static String timeAgoInWords(Date time) {
		return timeAgoInWords(new DateTime(time));
	}

	public static String getTime(DateTime date) {
		return date.toString("yyyy-MM-dd HH:mm:ss");
	}

	public static Integer parseTimeStringIntoSecs(String s) {
		if(s.contains("s")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s);
		} else if(s.contains("m")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s) * 60;
		} else if(s.contains("h")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s) * 3600;
		} else if(s.contains("d")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s) * 86400;
		} else if(s.contains("mo")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s) * 2592000;
		} else if(s.contains("y")) {
			s = s.replaceAll("[^\\d.]", "");
			return NumberUtil.parseInteger(s) * 31104000;
		} else {
			return NumberUtil.parseInteger(s);
		}
	}

	public static String formatSeconds(Integer seconds, String format) {
		format = format.toLowerCase();
		int hours = (int) Math.floor(seconds / 3600);
		int minutes = (int) Math.floor(seconds / 60);

		if(hours > 0) {
			String formatted = format.replaceAll("h", "" + hours);
			minutes = (int) Math.floor(seconds - hours * 3600 / 60);
			seconds = seconds - hours * 3600 - minutes * 60;
			if(minutes < 10) {
				formatted = formatted.replaceAll("m", "0" + minutes);
				if(seconds < 10) {
					formatted = formatted.replaceAll("s", "0" + seconds);
					return formatted;
				} else {
					formatted = formatted.replaceAll("s", "" + seconds);
					return formatted;
				}
			} else {
				formatted = formatted.replaceAll("m", "" + minutes);
				if(seconds < 10) {
					formatted = formatted.replaceAll("s", "0" + seconds);
					return formatted;
				} else {
					formatted = formatted.replaceAll("s", "" + seconds);
					return formatted;
				}
			}
		} else if(minutes > 0) {
			String formatted = format.replaceAll("h", "0");
			seconds = seconds - minutes * 60;
			if(minutes < 10) {
				formatted = formatted.replaceAll("m", "0" + minutes);
				if(seconds < 10) {
					formatted = formatted.replaceAll("s", "0" + seconds);
					return formatted;
				} else {
					formatted = formatted.replaceAll("s", "" + seconds);
					return formatted;
				}
			} else {
				formatted = formatted.replaceAll("m", "" + minutes);
				if(seconds < 10) {
					formatted = formatted.replaceAll("s", "0" + seconds);
					return formatted;
				} else {
					formatted = formatted.replaceAll("s", "" + seconds);
					return formatted;
				}
			}
		} else {
			String formatted = format.replaceAll("h", "0");
			formatted = formatted.replaceAll("m", "00");
			if(seconds < 10) {
				formatted = formatted.replaceAll("s", "0" + seconds);
				return formatted;
			} else {
				formatted = formatted.replaceAll("s", "" + seconds);
				return formatted;
			}
		}
	}
}
package com.philippelangevin.sdk.time;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
  * <p> Title: {@link TimeUtil} <p>
  * <p> Description: This class holds different date/time related methods. </p>
  * <p> Company : C-Tec <p>
  * 
  * @author plefebvre
  * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
  */

 /*
  * History
  * ------------------------------------------------
  * Date			Name		BT		Description
  * 2010-06-09		plefebvre
  */

public class TimeUtil {
	public static final double MILLISECONDS_TO_HOURS_MULTIPLIER = 1d / (1000 * 60 * 60);
	public static final double MILLISECONDS_TO_MINUTES_MULTIPLIER = 1d / (1000 * 60);
	
	/**
	 * An immutable final static time representing a disabled time.
	 */
	public static final Time DISABLED_TIME = new Time(Time.valueOf("00:00:00").getTime()) {
		private static final long serialVersionUID = -5977823024031164548L;
		@Override public void setDate(int i) {throw new UnsupportedOperationException();};
		@Override public void setHours(int i) {throw new UnsupportedOperationException();};
		@Override public void setMinutes(int i) {throw new UnsupportedOperationException();};
		@Override public void setMonth(int i) {throw new UnsupportedOperationException();};
		@Override public void setSeconds(int i) {throw new UnsupportedOperationException();};
		@Override public void setTime(long l) {throw new UnsupportedOperationException();};
		@Override public void setYear(int i) {throw new UnsupportedOperationException();};
	};
	
	/**
	 * Returns a double representing the number of hours elapsed between times.
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public static double getHoursElapsed(Time fromTime, Time toTime) {
		return getHoursElapsed(fromTime.getTime(), toTime.getTime());
	}
	
	/**
	 * Returns a double representing the number of hours elapsed between times.
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public static double getHoursElapsed(long fromTime, long toTime) {
		return (toTime - fromTime) * MILLISECONDS_TO_HOURS_MULTIPLIER;
	}
	
	/**
	 * Returns a double representing the number of hours of the specified time.
	 * @param t
	 * @return
	 */
	public static double getHours(Time t) {
		return getHoursElapsed(Time.valueOf("00:00:00"), t);
	}
	
	/**
	 * Returns a string representation of the provided hours value.
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public static String formatHoursElapsedString(Double hours) {
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
		formatSymbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#.###", formatSymbols);
		
		return format.format(hours);
	}
	
	/**
	 * Returns a Time object based on a number of hours.
	 * @param hours
	 * @return
	 */
	public static Time getTimeFromHours(Double hours) {
		//TODO There are definitely cleaner ways to do this!
		GregorianCalendar c = new GregorianCalendar(0, 0, 0, 0, 0, 0);
		int minutes = (int) Math.round(60*(hours-hours.intValue()));
		c.add(Calendar.HOUR, hours.intValue());
		c.add(Calendar.MINUTE, minutes);
		return new Time(c.getTime().getTime());
	}
	
	/**
	 * Returns the number of minutes contained in a 00:00 or 00:00:00 time format.
	 * @param timeString
	 * @return
	 */
	public static Integer getMinutesFromTimeString(String timeString) {
		if (timeString == null || timeString.length() != 5 && timeString.length() != 8) {
			return null;
		} else {
			// We remove the seconds (if any), they are not useful for us.
			if (timeString.length() == 8) {
				timeString = timeString.substring(0, 5);
			}
			
			try {
				String[] splits = timeString.split(":");
				return Integer.parseInt(splits[0]) * 60 + Integer.parseInt(splits[1]);
				
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * Returns the number of mintues contained in a time object.
	 * @param t
	 * @return
	 */
	public static Integer getMinutesFromTime(Time t) {
		// Time.toString() returns a 00:00:00 string representation.
		return getMinutesFromTimeString(String.valueOf(t));
	}
	
	public static Time getTimeFromMinutes(Integer minutes) {
		if (minutes == null) {
			return null;
		} else {
			return new Time(new GregorianCalendar(0, 0, 0, 0, minutes, 0).getTime().getTime());
		}
	}
	
	/**
	 * Returns the number of milliSeconds contained in a time object.
	 * @param t
	 * @return
	 */
	public static Time getTimeMilliSeconds(Long milliSeconds) {
		if (milliSeconds == null) {
			return null;
		} else {
			return new Time(milliSeconds);
		}
	}
	
	/**
	 * Returns the number of seconds contained in a time object.
	 * @param t
	 * @return
	 */
	public static Time getTimeSeconds(Integer seconds) {
		if (seconds == null) {
			return null;
		} else {
			return new Time(new GregorianCalendar(0, 0, 0, 0, 0, seconds).getTime().getTime());
		}
	}
	
	/**
	 * Similar to Time.valueOf(), but more flexible; allows incomplete time strings and decimals.
	 * See TimeUtil.main() for time parsing examples.
	 * @param o
	 * @return The parsed time, or 00:00:00 if it failed.
	 */
	public static Time parseTime(Object o) {
		return parseTime(o, DISABLED_TIME);
	}
	
	/**
	 * Similar to Time.valueOf(), but more flexible; allows incomplete time strings and decimals.
	 * See TimeUtil.main() for time parsing examples.
	 * @param o
	 * @param errorReplacement
	 * @return The parsed time, or errorReplacement if it failed.
	 */
	public static Time parseTime(Object o, Time errorReplacement) {
		if (o instanceof Time) {
			return (Time) o;
		} else {
			String s = String.valueOf(o);
			
			// We get rid of the decimal (if any).
			if (s.contains(".") || s.contains(",")) {
				String[] splits = s.split("\\.|\\,", -1);
				try {
					// 3.5 = 03:30:00
					s = splits[0] + ":" + (int)(Integer.parseInt((splits[1] + "00").substring(0, 2)) * 0.6);
				} catch (Exception e) {
					// ,5 = 00:30:00
					s = splits[0] + ":00";
				}
			}
			
			switch (s.length()) {
			case 1:
				// 3 = 03:00:00
				return parseTime("0" + s + ":00:00", errorReplacement);
			case 2:
				if (s.charAt(0) == ':') {
					// :3 = 00:30:00
					return parseTime("00" + s + "0:00", errorReplacement);
				} else if (s.charAt(1) == ':') {
					// 3: = 03:00:00
					return parseTime("0" + s + "00:00", errorReplacement);
				} else {
					// 03 = 03:00:00
					return parseTime(s + ":00:00", errorReplacement);
				}
			case 3:
				if (s.charAt(0) == ':') {
					// :30 = 00:30:00
					return parseTime("00" + s + ":00", errorReplacement);
				} else if (s.charAt(1) == ':') {
					// 3:3 = 03:30:00
					return parseTime("0" + s + "0:00", errorReplacement);
				} else if (s.charAt(2) == ':') {
					// 10: = 10:00:00
					return parseTime(s + "00:00", errorReplacement);
				} else {
					// 330 = 03:30:00
					return parseTime("0" + s.charAt(0) + ":" + s.substring(1) + ":00", errorReplacement);
				}
			case 4:
				if (s.charAt(1) == ':') {
					// 3:30 = 03:30:00
					return parseTime("0" + s + ":00", errorReplacement);
				} else if (s.charAt(2) == ':') {
					// 10:3 = 10:30:00
					return parseTime(s + "0:00", errorReplacement);
				} else {
					// 0330 = 03:30:00
					return parseTime(s.substring(0, 2) + ":" + s.substring(2) + ":00", errorReplacement);
				}
			case 5:
				// 03:30 = 03:30:00
				return parseTime(s + ":00", errorReplacement);
			case 6:
				// 033030 = 03:30:30
				return parseTime(s.substring(0, 2) + ":" + s.substring(2, 4) + ":" + s.substring(4, 6), errorReplacement);
			case 7:
				// 03:30:3 = 03:30:30
				return parseTime(s + "0", errorReplacement);
			case 8:
				// 03:30:30 = 03:30:30
				try {
					if (Integer.parseInt(s.replace(":", "")) >= 240000) {
						return errorReplacement;
					} else {
						return Time.valueOf(s);
					}
				} catch (Exception e) {
					return errorReplacement;
				}
			default:
				return errorReplacement;
			}
		}
	}
	
	/**
	 * Returns the current date added by the specified number of business days.
	 * Note that this method does not handle holidays.
	 * @param nbDays
	 * @return
	 */
	public static Date addBusinessDays(int nbDays) {
		return addBusinessDays(new Date(), nbDays);
	}
	
	/**
	 * Returns the specified date added by the specified number of business days.
	 * Note that this method does not handle holidays.
	 * @param fromDate
	 * @param nbDays
	 * @return
	 */
	public static Date addBusinessDays(Date fromDate, int nbDays) {
		if (nbDays < 0) {
			throw new IllegalArgumentException();
		} else if (fromDate == null) {
			throw new NullPointerException();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		for (int day = 0; day < nbDays; day++) {
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				calendar.add(Calendar.DATE, 3);
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				calendar.add(Calendar.DATE, 2);
			} else {
				calendar.add(Calendar.DATE, 1);
			}
		}
		
		return calendar.getTime();
	}
	
	/**
	 * Returns the current date and time.
	 * @return
	 */
	public static Timestamp now() {
		return new Timestamp(new Date().getTime());
	}
	
	/**
	 * Returns today's date with the time 00:00:00:000.
	 * @return
	 */
	public static Timestamp nowMidnight() {
		return setDateToMidnight(new Timestamp(new Date().getTime()));
	}
	
	/**
	 * Returns today's date and time with the format "dd.MMMMMMMMMM.yyyy HH:mm:ss".
	 * @return
	 */
	public static String nowTimeHour(){
		
		   SimpleDateFormat sdfTime = new SimpleDateFormat("dd.MMMMMMMMMM.yyyy HH:mm:ss");
		   Date now = new Date();

		   return sdfTime.format(now);
	}

	/**
	 * Sets the time of the date to 00:00:00:000.
	 * @param date The date to update, null is supported
	 * @return The same object that was passed in parameter, but updated.
	 */
	public static <T extends Date> T setDateToMidnight(T date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			
			date.setTime(calendar.getTimeInMillis());
			return date;
			
		} else {
			return date;
		}
	}
	
	public static void main(String[] args) throws Exception {
		// 1
		assertEquals(parseTime("3").toString(), "03:00:00");
		
		// 2
		assertEquals(parseTime(":3").toString(), "00:30:00");
		assertEquals(parseTime("3:").toString(), "03:00:00");
		assertEquals(parseTime("03").toString(), "03:00:00");
		
		// 3
		assertEquals(parseTime(":30").toString(), "00:30:00");
		assertEquals(parseTime("3:3").toString(), "03:30:00");
		assertEquals(parseTime("10:").toString(), "10:00:00");
		assertEquals(parseTime("330").toString(), "03:30:00");
		
		// 4
		assertEquals(parseTime("3:30").toString(), "03:30:00");
		assertEquals(parseTime("10:3").toString(), "10:30:00");
		assertEquals(parseTime("0330").toString(), "03:30:00");
		
		// 5
		assertEquals(parseTime("03:30").toString(), "03:30:00");
		
		// 6
		assertEquals(parseTime("033030").toString(), "03:30:30");
		
		// 8
		assertEquals(parseTime("03:30:30").toString(), "03:30:30");
		
		// Invalid
		assertEquals(parseTime("abc"), DISABLED_TIME);
		assertEquals(parseTime("abc", null), null);
		
		// Decimal
		assertEquals(parseTime(".5").toString(), "00:30:00");
		assertEquals(parseTime("3.").toString(), "03:00:00");
		assertEquals(parseTime("3,25").toString(), "03:15:00");
		assertEquals(parseTime("3.5").toString(), "03:30:00");
		assertEquals(parseTime("3,75").toString(), "03:45:00");
		assertEquals(parseTime("3.5000").toString(), "03:30:00");
		assertEquals(parseTime("3,80").toString(), "03:48:00");
		
		// All tests were successful!
		System.out.println("Success!");
	}
	
	/**
	 * This is used for testing purposes by the main method.
	 * This is similar to Assert.assertEquals(), but we want to avoid using JUnit package.
	 * @param o1
	 * @param o2
	 * @throws Exception
	 */
	private static void assertEquals(Object o1, Object o2) throws Exception {
		if (o1 == null && o2 != null || o1 != null && !o1.equals(o2)) {
			throw new Exception("Assertion failed: " + o1 + " != " + o2);
		}
	}
}

package com.pmtool.backend.util;

import java.time.LocalDateTime;

public class TimeUtil {
	public static int toMinutes(LocalDateTime dt) {
		return dt.getHour() * 60 + dt.getMinute();
	}

	public static String toTime(int minutes) {
		int h = minutes / 60;
		int m = minutes % 60;
		return String.format("%02d:%02d", h, m);
	}

	public static String formatDuration(int mins) {
		int h = mins / 60;
		int m = mins % 60;
		return h > 0 ? h + "h " + m + "m" : m + "m";
	}

	public static double formatDurationToDouble(Long millis) {
	    if (millis == null || millis == 0)
	        return 0.0;

	    double hours = millis / 3600000.0;
	    return Math.round(hours * 100.0) / 100.0;
	}


}

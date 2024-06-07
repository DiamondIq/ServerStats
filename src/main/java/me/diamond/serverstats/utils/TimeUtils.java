package me.diamond.serverstats.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static long getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }

    // Utility method to get the end of the day as milliseconds since epoch
    public static long getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        System.out.println(calendar.getTimeInMillis());
        return calendar.getTimeInMillis() + 1; // Add 1 millisecond to get the end of the day
    }

}

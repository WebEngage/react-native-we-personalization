package com.webengage.sdk.android.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollingDate {

    private static Pattern durationPattern = Pattern.compile("P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)S)?)?");
    private String duration;
    private long millis;

    public RollingDate(long millis, String duration) {
        this.millis = millis;
        this.duration = duration;
    }

    public RollingDate(Date date, String duration) {
        this(date.getTime(), duration);

    }

    public Date getTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(this.millis);
        if (duration != null) {
            Matcher matcher = durationPattern.matcher(this.duration);
            if (matcher.matches()) {
                calendar.add(Calendar.YEAR, parseNumber(matcher.group(1)));
                calendar.add(Calendar.MONTH, parseNumber(matcher.group(2)));
                calendar.add(Calendar.WEEK_OF_MONTH, parseNumber(matcher.group(3)));
                calendar.add(Calendar.DAY_OF_MONTH, parseNumber(matcher.group(4)));
                calendar.add(Calendar.HOUR, parseNumber(matcher.group(6)));
                calendar.add(Calendar.MINUTE, parseNumber(matcher.group(7)));
                calendar.add(Calendar.SECOND, parseNumber(matcher.group(8)));
                return calendar.getTime();
            }
        }
        return calendar.getTime();
    }

    private int parseNumber(String str) {
        if (str == null) {
            return 0;
        }
        try {
            if (str.startsWith("+")) {
                str = str.substring(1);
            }
            return Integer.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }
}

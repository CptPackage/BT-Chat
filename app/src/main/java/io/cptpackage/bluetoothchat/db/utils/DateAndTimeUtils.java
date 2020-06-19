package io.cptpackage.bluetoothchat.db.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateAndTimeUtils {
    private static final String TAG = "DateAndTimeUtils";
    private static SimpleDateFormat dateFormat;
    private static SimpleDateFormat compactDateFormat;
    private static SimpleDateFormat compactTimeFormat;
    private static SimpleDateFormat timeFormat;
    private static DateAndTimeUtils instance;

    private DateAndTimeUtils() {
        TimeZone timezone = TimeZone.getDefault();
        Locale locale = Locale.getDefault();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", locale);
        compactDateFormat = new SimpleDateFormat("dd-MM", locale);
        compactTimeFormat = new SimpleDateFormat("HH:mm", locale);
        timeFormat = new SimpleDateFormat("HH:mm:ss", locale);
        dateFormat.setTimeZone(timezone);
        timeFormat.setTimeZone(timezone);
        compactDateFormat.setTimeZone(timezone);
        compactTimeFormat.setTimeZone(timezone);
    }

    public static DateAndTimeUtils getInstance() {
        if (instance == null) {
            synchronized (DateAndTimeUtils.class) {
                if (instance == null) {
                    instance = new DateAndTimeUtils();
                }
            }
        }
        return instance;
    }

    public Date parseStringToDate(String target) {
        try {
            return dateFormat.parse(target);
        } catch (Exception ex) {
            Log.e(TAG, "parseStringToDate()", ex);
            return null;
        }
    }

    public String parseDateToString(Date date) {
        return dateFormat.format(date);
    }

    public String parseDateToStringCompacted(Date date) {
        return compactDateFormat.format(date);
    }

    public String parseTimeToStringCompacted(Date time) {
        return compactTimeFormat.format(time);
    }

    public Date parseStringToTime(String target) {
        try {
            return timeFormat.parse(target);
        } catch (ParseException ex) {
            Log.e(TAG, "parseStringToTime(): ", ex);
            return null;
        }
    }

    public String parseTimeToString(Date time) {
        return timeFormat.format(time);
    }

    public Date now() {
        return GregorianCalendar.getInstance().getTime();
    }
}

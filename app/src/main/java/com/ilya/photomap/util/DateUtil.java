package com.ilya.photomap.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getFormattedDate(long date) {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return format.format(new Date(date * 1_000));
    }

}

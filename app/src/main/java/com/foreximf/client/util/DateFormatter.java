package com.foreximf.client.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {

    /**
     *
     * @param dateString date array in JSON format
     * @param pattern date format intended
     * @return date string in new format
     */
    public static String format(String dateString, String pattern) {
        String tempDateString = "";
        try {
            tempDateString = new JSONObject(dateString).getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String newDateString = "";
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(tempDateString);
            SimpleDateFormat sdfOutput = new SimpleDateFormat(pattern, Locale.ENGLISH);
            sdfOutput.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
            newDateString = sdfOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    /**
     *
     * @param dateString date array in JSON format
     * @return date as a Date type
     */
    public static Date format(String dateString) {
//        String tempDateString = "";
//        try {
//            tempDateString = new JSONObject(dateString).getString("date");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Date date = new Date(System.currentTimeMillis());
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @param date date in Date type
     * @param pattern date format intended
     * @return date string in certain format
     */
    public static String format(Date date, String pattern) {
        String dateString;
        SimpleDateFormat sdfOutput = new SimpleDateFormat(pattern, Locale.ENGLISH);
        sdfOutput.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        dateString = sdfOutput.format(date);
        return dateString;
    }
}

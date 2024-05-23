package com.example.healthcareapp.utils;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

public class Time {

    public Time() {
    }

    public static String convert12to24(String time12) {
        //eg -> 2:30 PM to 14:30
        // Split the input time into hours, minutes, and AM/PM
        String[] timeParts = time12.split(" ");
        String[] hourMinute = timeParts[0].split(":");
        int hour = Integer.parseInt(hourMinute[0]);
        int minute = Integer.parseInt(hourMinute[1]);
        String amPm = timeParts[1];

        // Convert to 24-hour format
        if (amPm.equalsIgnoreCase("AM")) {
            if (hour == 12) {
                hour = 0;
            }
        } else if (amPm.equalsIgnoreCase("PM")) {
            if (hour != 12) {
                hour += 12;
            }
        }
        // Format the result
        return String.format("%02d:%02d", hour, minute);
    }

    public static String convert24to12(String time24h) {
        String[] parts = time24h.split(":");

        int hour_24h = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        if (0 <= hour_24h && hour_24h < 24 && 0 <= minute && minute < 60) {
            String period = (hour_24h < 12) ? "AM" : "PM";
            int hour_12h = (hour_24h == 0 || hour_24h == 12) ? 12 : hour_24h % 12;

            return String.format("%02d:%02d %s", hour_12h, minute, period);
        } else {
            return null;
        }
    }

    public static String extractStartTime (String input) {
        //eg -> 2:30 PM - 4:30 PM  to 2:30 PM
        // Split the input by -
        String[] parts = input.split("-");

        // Get the time before -
        String timeBeforeHyphen = parts[0].trim();

        return timeBeforeHyphen;
    }

    public static String extractEndTime (String input) {
        //eg -> 2:30 PM - 4:30 PM  to 4:30 PM
        // Split the input by -
        String[] parts = input.split("-");

        // Get the time before -
        String timeBeforeHyphen = parts[1].trim();

        return timeBeforeHyphen;
    }

    public static int[] extractDateTime(String dateString, String timeString) {
        //eg -> dateString -> 2024-01-01 to   year = 24, month = 01, day = 01
        //eg -> timeString -> 13:50 to    hour = 13, minute = 50 
        // Split date and time strings based on the delimiter
        String[] dateParts = dateString.split("-");
        String[] timeParts = timeString.split(":");

        // Convert substrings to integers
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        return new int[]{year, month, day, hour, minute};
    }

    public static String convertTimestampToString(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy - HH:mm:ss");
        String[] part = sdf.format(date).split("-");
        return part[0].trim() + " at " + Time.convert24to12(part[1].trim());
    }
}

package com.jalicz.MojeProduktyApp;

import java.time.LocalDateTime;
import java.time.Year;

public class Time {

    public static LocalDateTime get() {
        return LocalDateTime.now();
    }

    public static String getConsoleFormat() {
        final LocalDateTime time = get();
        final int h = time.getHour(), m = time.getMinute(), s = time.getSecond();
        return (h < 10 ? "0"+h : h) + ":" + (m < 10 ? "0"+m : m) + ":" + (s < 10 ? "0"+s : s) + " ";
    }

    public static String getLogFileFormat() {
        final LocalDateTime time = get();
        final int m = time.getMinute(), s = time.getSecond();
        return getDayOfWeekString(time.getDayOfWeek().getValue()) + " " + time.getDayOfMonth() + ". " + getMonthString(time.getMonthValue()) + " " +
                time.getYear() + " v " + time.getHour() + ":" + (m < 10 ? "0"+m : m) + ":" + (s < 10 ? "0"+s : s);
    }

    public static String getProductFileFormat() {
        final LocalDateTime time = get();
        return time.getDayOfMonth() + "-" + time.getMonthValue() + "-" + time.getYear() + "/" + time.getHour() + "-" + time.getMinute() + "-" + time.getSecond();
    }

    private static String getDayOfWeekString(int value) {
        return switch (value) {
          case 1 -> "pondělí";
          case 2 -> "úterý";
          case 3 -> "středu";
          case 4 -> "čtvrtek";
          case 5 -> "pátek";
          case 6 -> "sobotu";
          case 7 -> "neděli";
          default -> "Neplatný den";
        };
    }

    private static String getMonthString(int value) {
        return switch (value) {
            case 1 ->  "ledna";
            case 2 ->  "února";
            case 3 ->  "března";
            case 4 ->  "dubna";
            case 5 ->  "května";
            case 6 ->  "června";
            case 7 ->  "července";
            case 8 ->  "srpna";
            case 9 ->  "září";
            case 10 -> "října";
            case 11 -> "listopadu";
            case 12 -> "prosince";
            default -> "Neplatný měsíc";
        };
    }

    public static boolean isValidDate(int day, int month /* 1 - 12 */, int year, boolean ignoreInvalidMonth) {
        if(day < 1) return false;
        if(!ignoreInvalidMonth && (month < 1 || month > 12)) return false;
        return switch (month) {
            case 2 -> day <= (isLeapYear(year) || year == -1 ? 29 : 28);
            case 4, 11, 9, 6 -> day <= 30;
            default -> day <= 31;
        };
    }

    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }
}

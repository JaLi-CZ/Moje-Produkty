package com.jalicz.MojeProduktyApp.files;

import com.jalicz.MojeProduktyApp.GUI.panels.ConsolePanel;
import com.jalicz.MojeProduktyApp.Time;
import com.jalicz.MojeProduktyApp.files.FileManager;

public class Log {

    private static final String
            errorPrefix = "[Chyba]: ", errorColor = "#ff2200",
            warningPrefix = "[Varování]: ", warningColor = "#fff200",
            infoPrefix = "[Info]: ", infoColor = "#00eeff",
            defaultColor = "#ffffff",
            newLine = "<br>";

    private static final int ERROR = 1, WARNING = 2, INFO = 0;

    private static String colorize(String s, String color) {
        return "<span style='color:" + color + "'>" + s + "</span>";
    }

    private static String getColorByMessageType(int messageType) {
        return switch (messageType) {
            case INFO -> infoColor;
            case WARNING -> warningColor;
            case ERROR -> errorColor;
            default -> "";
        };
    }

    private static String getPrefixByMessageType(int messageType) {
        return switch (messageType) {
            case INFO -> infoPrefix;
            case WARNING -> warningPrefix;
            case ERROR -> errorPrefix;
            default -> "";
        };
    }

    private static void log(String s, int messageType) {
        String prefix = getPrefixByMessageType(messageType), time = Time.getConsoleFormat();

        System.out.println(time + prefix + s);
        FileManager.log(time + prefix + s);

        if(messageType == ERROR) s = s.replaceAll("\n", newLine);
        prefix = colorize(prefix, getColorByMessageType(messageType));
        time = colorize(time, "#99ff45");
        s = colorize(s, defaultColor);
        ConsolePanel.appendText(time + prefix + s + newLine);
    }

    public static void error(String s, Exception e) {
        log(s + "\n- Název chyby: " + e.getClass().getSimpleName() + "\n- Podrobnosti: " + e.getLocalizedMessage(), ERROR);
    }

    public static void error(String s) {
        log(s, ERROR);
    }

    public static void warn(String s) {
        log(s, WARNING);
    }

    public static void info(String s) {
        log(s, INFO);
    }
}

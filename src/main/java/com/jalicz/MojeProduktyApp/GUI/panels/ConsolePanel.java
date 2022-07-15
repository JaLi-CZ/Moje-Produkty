package com.jalicz.MojeProduktyApp.GUI.panels;

import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.TextArea;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ConsolePanel extends Panel {

    private static final int maxChars = 20000, removeCharsWhenFull = 2000;
    private static final ArrayList<ConsolePanel> instances = new ArrayList<>();

    private static StringBuilder builder = new StringBuilder();
    private final TextArea console;

    public ConsolePanel() {
        super(new GridLayout());
        console = new TextArea();
        JScrollPane scroll = new JScrollPane(console);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        instances.add(this);

        console.setEditable(false);
        add(scroll);
    }

    public static void appendText(String s) {
        builder.append(s);
        if(builder.length() > maxChars) builder = new StringBuilder(builder.substring(removeCharsWhenFull));
        for(ConsolePanel instance: instances) {
            final String text = "<div style='font-size:20px'>" + builder.toString() + "</div>";
            instance.console.setText(text);
        }
    }
}

package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import java.awt.*;

public class Text extends JLabel {

    private static final Color color = Color.WHITE;

    public Text(String text) {
        setText(text);
        setForeground(color);
        setFont(Frame.getFont(1));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    }
}

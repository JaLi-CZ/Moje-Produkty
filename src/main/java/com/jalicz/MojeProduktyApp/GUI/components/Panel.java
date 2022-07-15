package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private static final Color background = new Color(20, 20, 20);

    private final LayoutManager layout;

    public Panel(LayoutManager layout, Color background, int w, int h) {
        this.layout = layout;
        setBackground(background);
        setLayout(layout);
        setSize(w, h);
    }

    public Panel(LayoutManager layout, int w, int h) {
        this(layout, background, w, h);
    }

    public Panel(LayoutManager layout) {
        this(layout, 1, 1);
    }
}

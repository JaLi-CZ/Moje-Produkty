package com.jalicz.MojeProduktyApp.GUI.components;

import com.jalicz.MojeProduktyApp.files.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Frame extends JFrame {

    private static final BufferedImage icon = FileManager.getImage("ikony/MPx256.png");

    public final Panel rootPanel;

    public Frame(Panel rootPanel, String title, int w, int h) {

        this.rootPanel = rootPanel;

        setIconImage(icon);
        setTitle(title);
        setSize(w, h);
        setLocationRelativeTo(null);

        add(rootPanel);
    }

    public static Font getFont(double size) {
        return new Font("", Font.PLAIN, (int) (30 * size));
    }

    @Override
    public void pack() {
        final boolean maximized = getExtendedState() == JFrame.MAXIMIZED_BOTH;
        final int w = getWidth(), h = getHeight();
        super.pack();
        if(maximized) setExtendedState(JFrame.MAXIMIZED_BOTH);
        else setSize(w, h);
    }

    public void packNormally() {
        super.pack();
    }

    public void destroy() {
        setVisible(false);
        dispose();
    }
}

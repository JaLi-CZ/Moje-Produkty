package com.jalicz.MojeProduktyApp.GUI.components;

import com.jalicz.MojeProduktyApp.files.FileManager;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Image extends JLabel {

    private final BufferedImage image;
    private final double scale;

    public Image(String filePath, double scale) {
        this.scale = scale;
        image = FileManager.getImage(filePath);
        if(image == null) return;
        setImageBounds();
    }

    public void setImageBounds() {
        final int w = (int) (image.getWidth()*scale), h = (int) (image.getHeight()*scale);
        setIcon(new ImageIcon(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH)));
        setSize(w, h);
    }
}

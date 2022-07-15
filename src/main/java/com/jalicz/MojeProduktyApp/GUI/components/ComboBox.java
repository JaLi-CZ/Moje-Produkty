package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class ComboBox extends Panel {

    private static final Color
            background = Color.BLACK,
            foreground = new Color(255, 89, 0),
            border = new Color(255, 60, 0);

    private static final Border dropboxBorder = BorderFactory.createCompoundBorder(new LineBorder(border),
            BorderFactory.createMatteBorder(5, 5, 5, 5, background));

    public final Text label;
    public final JComboBox<String> comboBox;

    public ComboBox(String label, String[] options) {
        super(new BorderLayout(9, 9));

        this.label = new Text(label);
        this.comboBox = new JComboBox<>(options);

        this.label.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 9));
        comboBox.setForeground(foreground);
        comboBox.setBackground(background);
        comboBox.setBorder(new LineBorder(border, 1));
        comboBox.setFont(Frame.getFont(0.8));
        comboBox.setFocusable(false);
        ((BasicComboPopup)comboBox.getAccessibleContext().getAccessibleChild(0)).setBorder(dropboxBorder);

        setBorder(null);

        add(this.label, BorderLayout.WEST);
        add(comboBox, BorderLayout.CENTER);
    }
}

package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextField extends Panel {

    private static final Color
            background = new Color(9, 9, 9),
            focusedBackground = new Color(50, 50, 50),
            labelForeground = Color.WHITE,

            fieldForeground = Color.WHITE,
            fieldBorderColor = new Color(0, 248, 200),
            fieldBackground = Color.BLACK,

            caretColor = new Color(105, 255, 220);

    public Text label;
    public JTextField textField;

    public TextField(String label, int width) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.label = new Text(label);
        this.label.setForeground(labelForeground);
        this.label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        textField = new JTextField();
        textField.setBackground(fieldBackground);
        textField.setForeground(fieldForeground);
        textField.setBorder(new LineBorder(fieldBorderColor, 1));
        textField.setPreferredSize(new Dimension(width, 43));
        textField.setFont(Frame.getFont(0.9));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setCaretColor(caretColor);

        setBackground(background);

        add(this.label);
        add(textField);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(focusedBackground);
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBackground(background);
            }
        });
    }

    public void increment() {
        try {
            int value = Integer.parseInt(textField.getText());
            textField.setText(++value+"");
        } catch (Exception ignored) { }
    }
}

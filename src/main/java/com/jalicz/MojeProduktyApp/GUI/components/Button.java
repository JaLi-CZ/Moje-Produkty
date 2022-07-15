package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class Button extends JButton {

    private static class FixedButtonModel extends DefaultButtonModel {

        private FixedButtonModel() { }

        @Override
        public boolean isPressed() {
            return false;
        }

        @Override
        public boolean isRollover() {
            return false;
        }

        @Override
        public void setRollover(boolean b) { }
    }

    public static final Color
            background = new Color(0, 0, 0),
            backgroundFocused = new Color(40, 40, 40),
            backgroundPressed = new Color(100, 100, 100),
            foreground = new Color(145, 248, 0),
            selectedColor = new Color(36, 61, 0),
            selectedFocusedColor = new Color(70, 70, 70),
            selectedPressedColor = new Color(120, 120, 120);

    private static final Border border = new LineBorder(foreground, 1);

    public boolean focused = false;
    public boolean selected = false;

    public Button(String text) {
        setText(text);
        setBorder(border);
        setBackground(background);
        setForeground(foreground);
        setFont(Frame.getFont(1));
        setFocusPainted(false);
        setModel(new FixedButtonModel());
        setMargin(new Insets(0, 0, 0, 0));
        setFocusable(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(selected ? selectedFocusedColor : backgroundFocused);
                focused = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(selected ? selectedColor : background);
                focused = false;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(selected ? selectedPressedColor : backgroundPressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(focused) setBackground(selected ? selectedFocusedColor : backgroundFocused);
                else setBackground(selected ? selectedColor : background);
            }
        });
    }
}

package com.jalicz.MojeProduktyApp.GUI.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class CheckBox extends Panel {

    private static final Color
            background = new Color(9, 9, 9),
            textColor = Color.WHITE,

            boxBackground = Color.BLACK,
            boxSelected = Color.GREEN,
            boxUnselected = Color.RED;

    private static final Border
            selectedBorder =
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(boxSelected, 1),
                        BorderFactory.createLineBorder(boxBackground, 3)
                ),
            unselectedBorder =
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(boxUnselected, 1),
                            BorderFactory.createLineBorder(boxBackground, 3)
                    );

    public final Button checkBox;
    public boolean selected;

    public CheckBox(String text, boolean selected) {
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));

        this.checkBox = new Button("");
        Text text1 = new Text(text);

        setBackground(background);
        text1.setForeground(textColor);
        text1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        this.checkBox.setBackground(boxBackground);

        checkBox.addActionListener(e -> {
            this.selected = !this.selected;
            setSelected(this.selected);
            onCheckAction(this.selected);
        });

        setSelected(selected);

        add(text1);
        add(checkBox);
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            checkBox.setBorder(selectedBorder);
            checkBox.setForeground(boxSelected);
            checkBox.setText("Ano");
        } else {
            checkBox.setBorder(unselectedBorder);
            checkBox.setForeground(boxUnselected);
            checkBox.setText("Ne");
        }
    }

    public abstract void onCheckAction(boolean checked);
}

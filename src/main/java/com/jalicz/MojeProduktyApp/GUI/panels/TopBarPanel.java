package com.jalicz.MojeProduktyApp.GUI.panels;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.frames.MenuFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TopBarPanel extends Panel {

    private static final Border padding = BorderFactory.createEmptyBorder(5, 8, 8, 5);

    private final MenuFrame menuFrame;
    private final Button menu, manageProducts, console, programGuide;

    public TopBarPanel(MenuFrame menuFrame) {
        super(new GridLayout());

        this.menuFrame = menuFrame;

        menu = new Button("Menu");
        manageProducts = new Button("Správa produktů");
        console = new Button("Konzole");
        programGuide = new Button("Příručka");

        menu.selected = true;
        menu.setBackground(Button.selectedColor);

        menu.setBorder(padding);
        manageProducts.setBorder(padding);
        console.setBorder(padding);
        programGuide.setBorder(padding);
        
        menu.addActionListener(e -> {
            resetPanelsColor();
            menuFrame.changePanel(menuFrame.menuPanel);
            menu.selected = true;
            menu.setBackground(Button.selectedColor);
        });

        manageProducts.addActionListener(e -> {
            resetPanelsColor();
            menuFrame.changePanel(menuFrame.manageProductsPanel);
            manageProducts.selected = true;
            manageProducts.setBackground(Button.selectedColor);
        });

        console.addActionListener(e -> {
                resetPanelsColor();
                menuFrame.changePanel(menuFrame.consolePanel);
                console.selected = true;
                console.setBackground(Button.selectedColor);
        });

        programGuide.addActionListener(e -> {
            resetPanelsColor();
            menuFrame.changePanel(menuFrame.programGuidePanel);
            programGuide.selected = true;
            programGuide.setBackground(Button.selectedColor);
        });

        add(menu);
        add(manageProducts);
        add(console);
        add(programGuide);
    }

    private void resetPanelsColor() {
        menu.setBackground(Button.background);
        manageProducts.setBackground(Button.background);
        console.setBackground(Button.background);
        programGuide.setBackground(Button.background);

        menu.selected = false;
        manageProducts.selected = false;
        console.selected = false;
        programGuide.selected = false;
    }
}

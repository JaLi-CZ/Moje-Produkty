package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddItemErrorFrame extends Frame {

    private static AddItemErrorFrame lastInstance;

    public AddItemErrorFrame(ArrayList<String> errors) {
        super(new Panel(new BorderLayout()), "Chybové hlášení - Přidávání předmětu", Screen.ofWidth(0.4), Screen.ofHeight(0.3));

        if(lastInstance != null) lastInstance.destroy();
        lastInstance = this;

        final Text title = new Text("Ve vyplněných údajích prosím opravte tyto chyby:");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.CYAN);

        final JTextArea area = new JTextArea();
        final JScrollPane scroll = new JScrollPane(area);
        scroll.getVerticalScrollBar().setUnitIncrement(5);

        final StringBuilder builder = new StringBuilder();
        for(String error: errors) builder.append("- ").append(error).append("\n");

        area.setEditable(false);
        area.setText(builder.toString());
        area.setFont(Frame.getFont(0.8));
        area.setBackground(Color.BLACK);
        area.setForeground(Color.RED);
        area.setLineWrap(true);
        area.setPreferredSize(new Dimension(getWidth(), 250));

        final Button ok = new Button("Dobře, pokusím se.");

        rootPanel.add(title, BorderLayout.NORTH);
        rootPanel.add(scroll, BorderLayout.CENTER);
        rootPanel.add(ok, BorderLayout.SOUTH);

        packNormally();
        setVisible(true);

        ok.addActionListener(e -> destroy());
    }
}

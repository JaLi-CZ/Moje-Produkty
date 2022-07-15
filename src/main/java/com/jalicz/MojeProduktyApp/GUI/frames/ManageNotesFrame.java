package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.TextField;
import com.jalicz.MojeProduktyApp.GUI.components.*;
import com.jalicz.MojeProduktyApp.GUI.panels.ManageItemsPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class ManageNotesFrame extends Frame {

    private static ManageNotesFrame lastInstance;

    private final ArrayList<TextField> fields;
    private final Panel noteList;
    private final Text title;

    public ManageNotesFrame(ManageItemsPanel manageItemsPanel) {
        super(new Panel(new BorderLayout()), "Správa poznámek", Screen.ofWidth(0.3), Screen.ofHeight(0.4));

        if(lastInstance != null) lastInstance.destroy();
        lastInstance = this;

        this.fields = new ArrayList<>();
        if(manageItemsPanel.currentNotes != null) {
            int i = 0;
            for(String note: manageItemsPanel.currentNotes) {
                final TextField field = new TextField(++i + ".", 390);
                field.textField.setText(note);
                fields.add(field);
            }
        }

        title = new Text("Poznámky");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.ORANGE);

        // noteList panel
        {
            noteList = new Panel(null);
            updateList();
        }
        JScrollPane noteListScroll = new JScrollPane(noteList);
        noteListScroll.getVerticalScrollBar().setUnitIncrement(7);
        noteListScroll.setPreferredSize(new Dimension(450, 400));

        final Panel buttonsPanel;
        {
            buttonsPanel = new Panel(new GridLayout(2, 2));

            final Button add = new Button("Přidat poznámku");
            final Button remove = new Button("Odebrat poznámku");
            final Button cancel = new Button("Zahodit změny");
            final Button save = new Button("Uložit změny");

            add.setBorder(new LineBorder(Color.CYAN, 1));
            add.setForeground(Color.CYAN);

            remove.setBorder(new LineBorder(Color.ORANGE, 1));
            remove.setForeground(Color.ORANGE);

            cancel.setBorder(new LineBorder(Color.RED, 1));
            cancel.setForeground(Color.RED);

            save.setBorder(new LineBorder(Color.GREEN, 1));
            save.setForeground(Color.GREEN);

            buttonsPanel.add(remove);
            buttonsPanel.add(add);
            buttonsPanel.add(cancel);
            buttonsPanel.add(save);

            cancel.addActionListener(e -> destroy());

            save.addActionListener(e -> {
                for(TextField field: fields) {
                    if(field.textField.getText().isEmpty()) {
                        new AllNoteFieldsNotFilledErrorFrame();
                        return;
                    }
                }
                final ArrayList<String> notes = new ArrayList<>();
                for(TextField field: fields) notes.add(field.textField.getText());
                manageItemsPanel.currentNotes = notes.isEmpty() ? null : notes;
                destroy();
            });

            add.addActionListener(e -> {
                final TextField field = new TextField((fields.size()+1) + ".", 390);
                field.textField.setText("");
                fields.add(field);
                updateList();
            });

            remove.addActionListener(e -> {
                if(!fields.isEmpty()) fields.remove(fields.size() - 1);
                updateList();
            });
        }

        rootPanel.add(title, BorderLayout.NORTH);
        rootPanel.add(noteListScroll, BorderLayout.CENTER);
        rootPanel.add(buttonsPanel, BorderLayout.SOUTH);

        packNormally();
        setVisible(true);
    }

    public void updateList() {
        noteList.removeAll();
        noteList.setLayout(new GridLayout(fields.size(), 1));
        for(TextField field: fields) noteList.add(field);
        final int len = fields.size();
        title.setText("Poznámky (" + (len == 0 ? "Žádné" : len+"") + ")");
        noteList.updateUI();
    }
}

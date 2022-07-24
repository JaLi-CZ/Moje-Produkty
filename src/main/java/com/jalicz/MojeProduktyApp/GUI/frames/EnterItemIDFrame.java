package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.Screen;
import com.jalicz.MojeProduktyApp.GUI.components.TextField;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.search.SearchEngine;
import com.jalicz.MojeProduktyApp.model.TypeID;

import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public abstract class EnterItemIDFrame extends Frame {

    private static EnterItemIDFrame instance;

    public EnterItemIDFrame() {
        super(new Panel(new GridLayout(2, 1)), "Zadejte ID předmětu", Screen.ofWidth(0.25), Screen.ofHeight(0.3));

        if(instance != null) instance.destroy();
        instance = this;

        TextField field = new TextField("ID předmětu:", 120);

        Panel buttonsPanel;
        Button ok, cancel;
        {
            buttonsPanel = new Panel(new GridLayout(1, 2));

            ok = new Button("Potvrdit");
            ok.setForeground(Color.GREEN);
            ok.setBorder(new LineBorder(Color.GREEN));

            cancel = new Button("Zrušit");
            cancel.setForeground(Color.RED);
            cancel.setBorder(new LineBorder(Color.RED));

            buttonsPanel.add(ok);
            buttonsPanel.add(cancel);
        }

        rootPanel.add(field);
        rootPanel.add(buttonsPanel);

        packNormally();
        setVisible(true);

        field.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = field.textField.getText();
                int id = -1;
                try { id = Integer.parseInt(text); } catch (Exception ignored) {}
                field.textField.setForeground(id > 0 && SearchEngine.isItemExisting(id, TypeID.INVALID) ? Color.GREEN : Color.RED);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = field.textField.getText();
                int id = -1;
                try { id = Integer.parseInt(text); } catch (Exception ignored) {}
                field.textField.setForeground(id > 0 && SearchEngine.isItemExisting(id, TypeID.INVALID) ? Color.GREEN : Color.RED);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                String text = field.textField.getText();
                int id = -1;
                try { id = Integer.parseInt(text); } catch (Exception ignored) {}
                field.textField.setForeground(id > 0 && SearchEngine.isItemExisting(id, TypeID.INVALID) ? Color.GREEN : Color.RED);
            }
        });

        cancel.addActionListener(e -> {
            onIDEntered(-1);
            destroy();
        });

        ok.addActionListener(e -> {
            int id = -1;
            try { id = Integer.parseInt(field.textField.getText()); } catch (Exception ignored) {}
            if(!SearchEngine.isItemExisting(id, TypeID.INVALID)) id = -1;
            if(id == -1) Log.error("Spravovat předmět: Bylo zadáno špatné ID předmětu. Pouze předměty, které existují, je možné spravovat.");
            onIDEntered(id);
            destroy();
        });
    }

    public abstract void onIDEntered(int id);
}

package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.*;
import com.jalicz.MojeProduktyApp.GUI.panels.ManageItemsPanel;
import com.jalicz.MojeProduktyApp.files.FileManager;

import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;

public class ManageItemsSettingsFrame extends Frame {

    public static final String
            yes = "ano", no = "ne",
            deleteAllAfterAddition = "vycistit-vse-po-pridani-predmetu",
            incrementIDAfterAddition = "inkrementovat-ID-po-pridani-predmetu",
            clearNotesAfterAddition = "vycistit-poznamky-po-pridani-predmetu",
            autoExpirationFieldSwitch = "po-spravnem-zadani-expirace-automaticky-zamerit-dalsi-pole";

    private static ManageItemsSettingsFrame lastInstance;

    public ManageItemsSettingsFrame() {
        super(new Panel(new GridLayout(5, 1)), "Nastavení přidávání předmětů", Screen.ofWidth(0.4), Screen.ofHeight(0.4));

        if(lastInstance != null) lastInstance.destroy();
        lastInstance = this;

        final HashMap<String, String> map = FileManager.getAppFileMap(FileManager.addItemSettingsFilePath);

        final CheckBox deleteAll = new CheckBox("Po přidání produktu vyčistit pole:",
                map != null && map.getOrDefault(deleteAllAfterAddition, no).equals(yes)) {
            @Override
            public void onCheckAction(boolean checked) { }
        };

        final CheckBox incrementID = new CheckBox("Po přidání produktu inkrementovat ID předmětu:",
                map != null && map.getOrDefault(incrementIDAfterAddition, no).equals(yes)) {
            @Override
            public void onCheckAction(boolean checked) { }
        };

        final CheckBox clearNotes = new CheckBox("Po přidání předmětu vyčistit zapsané poznámky:",
                map != null && map.getOrDefault(clearNotesAfterAddition, no).equals(yes)) {
            @Override
            public void onCheckAction(boolean checked) { }
        };

        final CheckBox autoExpirationSwitch = new CheckBox("Po vyplnění automaticky zameřit na další pole datumu expirace:",
                map != null && map.getOrDefault(autoExpirationFieldSwitch, no).equals(yes)) {
            @Override
            public void onCheckAction(boolean checked) { }
        };

        final Panel buttonsPanel;
        {
            buttonsPanel = new Panel(new GridLayout(1, 2));

            final Button save = new Button("Uložit změny");
            save.setForeground(Color.GREEN);
            save.setBorder(new LineBorder(Color.GREEN, 1));

            final Button cancel = new Button("Zahodit změny");
            cancel.setForeground(Color.RED);
            cancel.setBorder(new LineBorder(Color.RED, 1));

            buttonsPanel.add(save);
            buttonsPanel.add(cancel);

            save.addActionListener(e -> {
                final HashMap<String, String> currentMap = new HashMap<>();
                currentMap.put(deleteAllAfterAddition, deleteAll.selected ? yes : no);
                currentMap.put(incrementIDAfterAddition, incrementID.selected ? yes : no);
                currentMap.put(clearNotesAfterAddition, clearNotes.selected ? yes : no);
                currentMap.put(autoExpirationFieldSwitch, autoExpirationSwitch.selected ? yes : no);
                FileManager.saveAppFileMap(FileManager.addItemSettingsFilePath, currentMap);
                ManageItemsPanel.settingsMap = currentMap;
                destroy();
            });

            cancel.addActionListener(e -> destroy());
        }

        rootPanel.add(deleteAll);
        rootPanel.add(incrementID);
        rootPanel.add(clearNotes);
        rootPanel.add(autoExpirationSwitch);
        rootPanel.add(buttonsPanel);

        packNormally();
        setSize(getWidth()+18, getHeight());
        setVisible(true);
    }
}

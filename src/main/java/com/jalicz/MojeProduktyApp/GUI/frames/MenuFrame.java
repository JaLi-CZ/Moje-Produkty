package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.*;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.panels.*;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.WebServer;
import com.jalicz.MojeProduktyApp.files.FileManager;

import java.awt.*;
import java.util.HashMap;

public class MenuFrame extends Frame {

    public final Panel
            menuTopBar,
            menuPanel, manageProductsPanel, consolePanel, programGuidePanel;
    public Panel lastChangedPanel = null;

    private final WebServer server;

    public MenuFrame() {
        super(new Panel(new BorderLayout()), "Moje Produkty", Screen.ofWidth(0.5), Screen.ofHeight(0.5));

        this.server = new WebServer();

        menuTopBar = new TopBarPanel(this);
        menuPanel = new MenuPanel(server);
        manageProductsPanel = new ManageItemsPanel();
        consolePanel = new ConsolePanel();
        programGuidePanel = new ProgramGuidePanel();

        rootPanel.add(menuTopBar, BorderLayout.NORTH);
        changePanel(manageProductsPanel);
        packNormally();
        changePanel(menuPanel);

        Log.info("Otevírám hlavní okno aplikace...");
        setVisible(true);

        // in case, that auto-launch server is on
        final HashMap<String, String> serverSettings = FileManager.getAppFileMap(FileManager.serverSettingsFilePath);
        if(serverSettings != null) {
            final String value = serverSettings.get(ServerSettingsFrame.launchServerOnProgramStartup);
            if(value != null && value.equals(ServerSettingsFrame.yes)) ((MenuPanel) menuPanel).clickServerButton();
        }
    }

    public void changePanel(Panel panel) {
        if(lastChangedPanel != null) rootPanel.remove(lastChangedPanel);
        lastChangedPanel = panel;
        rootPanel.add(panel, BorderLayout.CENTER);
        panel.updateUI();
        pack();
    }
}
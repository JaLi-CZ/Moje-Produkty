package com.jalicz.MojeProduktyApp.GUI.frames;

import com.jalicz.MojeProduktyApp.GUI.components.*;
import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.TextField;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.PortManager;
import com.jalicz.MojeProduktyApp.files.FileManager;

import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class ServerSettingsFrame extends Frame {

    public static final String
            yes = "ano", no = "ne",
            launchProgramOnPCStartup = "zapnout-program-po-spusteni-pocitace",
            launchServerOnProgramStartup = "zapnout-server-po-spusteni-programu",
            password = "heslo";

    private static boolean currentlyVisible = false;
    private static ServerSettingsFrame lastInstance;

    private TextField port;

    public ServerSettingsFrame() {
        super(new Panel(new GridLayout(5, 1)), "Nastavení serveru", Screen.ofWidth(0.4), Screen.ofHeight(0.3));

        if(currentlyVisible) {
            lastInstance.setState(NORMAL);
            lastInstance.setVisible(true);
            destroy();
            return;
        }

        lastInstance = this;
        currentlyVisible = true;

        final HashMap<String, String> serverSettings = FileManager.getAppFileMap(FileManager.serverSettingsFilePath);

        // FIX >> IMPLEMENT THIS
        final CheckBox launchProgramAutomatically = new CheckBox("Zapnout program po spuštění počítače:",
                serverSettings != null && serverSettings.getOrDefault(launchProgramOnPCStartup, no).equals(yes)) {
            public void onCheckAction(boolean checked) { }
        };

        final CheckBox launchServerOnStartup = new CheckBox("Zapnout server po spuštění programu:",
                serverSettings != null && serverSettings.getOrDefault(launchServerOnProgramStartup, no).equals(yes)) {
            public void onCheckAction(boolean checked) { }
        };

        port = new TextField("Port:", 230);
        if(serverSettings != null) port.textField.setText(serverSettings.getOrDefault("port", PortManager.defaultPort+""));
        port.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPort();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPort();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkPort();
            }
        });
        checkPort();

        final Panel passwordPanel;
        final TextField passwordField;
        final CheckBox usePassword;
        {
            passwordPanel = new Panel(new BorderLayout());

            passwordField = new TextField("Heslo:", 310);

            final String savedPsw = serverSettings == null ? null : serverSettings.getOrDefault(password, null);

            usePassword = new CheckBox("Požadovat heslo:", savedPsw != null) {
                @Override
                public void onCheckAction(boolean checked) {
                    passwordField.setVisible(checked);
                }
            };
            if(!usePassword.selected) passwordField.setVisible(false);
            else passwordField.textField.setText(savedPsw);

            passwordPanel.add(usePassword, BorderLayout.WEST);
            passwordPanel.add(passwordField, BorderLayout.CENTER);
        }

        final Panel actionPanel;
        final Button cancel, save;
        {
            actionPanel = new Panel(new GridLayout(1, 2));

            cancel = new Button("Zahodit změny");
            save = new Button("Uložit změny");

            cancel.setBorder(new LineBorder(Color.RED, 1));
            cancel.setForeground(Color.RED);

            save.setBorder(new LineBorder(Color.GREEN, 1));
            save.setForeground(Color.GREEN);

            actionPanel.add(BorderLayout.CENTER, cancel);
            actionPanel.add(BorderLayout.CENTER, save);

            cancel.addActionListener(e -> {
                destroy();
                currentlyVisible = false;
            });

            save.addActionListener(e -> {
                final HashMap<String, String> map = new HashMap<>();
                map.put(launchProgramOnPCStartup, launchProgramAutomatically.selected ? yes : no);
                map.put(launchServerOnProgramStartup, launchServerOnStartup.selected ? yes : no);

                final int portInt = isValidPort(port.textField.getText());
                if(portInt != -1) map.put("port", portInt+"");
                else map.put("port", null);

                final String psw = passwordField.textField.getText();
                if(usePassword.selected && psw != null && !psw.isEmpty() && !psw.contains(" ")) map.put(password, psw);

                FileManager.saveAppFileMap(FileManager.serverSettingsFilePath, map);

                Log.info("Bylo změněno nastavení webového serveru.");

                destroy();
                currentlyVisible = false;
            });
        }

        rootPanel.add(launchProgramAutomatically);
        rootPanel.add(launchServerOnStartup);
        rootPanel.add(port);
        rootPanel.add(passwordPanel);
        rootPanel.add(actionPanel);

        boolean passwordUnselected = !usePassword.selected;
        if(passwordUnselected) usePassword.checkBox.doClick();
        packNormally();
        if(passwordUnselected) usePassword.checkBox.doClick();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                currentlyVisible = false;
            }
        });
    }

    private void checkPort() {
        final String text = port.textField.getText();
        final int validation = isValidPort(text);

        int portInt = -1;
        try {
            portInt = Integer.parseInt(text);
        } catch (Exception ignored) { }

        if(portInt != -1 && portInt == PortManager.currentServerPort) port.textField.setForeground(Color.YELLOW);
        else if(validation == -1) port.textField.setForeground(Color.RED);
        else port.textField.setForeground(Color.GREEN);
    }

    // -1 == not valid
    private static int isValidPort(String portStr) {
        final int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (Exception e) {
            return -1;
        }
        return PortManager.isPortAvialable(port) ? port : -1;
    }
}

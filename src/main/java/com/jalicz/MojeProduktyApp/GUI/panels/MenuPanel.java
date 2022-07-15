package com.jalicz.MojeProduktyApp.GUI.panels;

import com.jalicz.MojeProduktyApp.GUI.components.Button;
import com.jalicz.MojeProduktyApp.GUI.components.Frame;
import com.jalicz.MojeProduktyApp.GUI.components.Image;
import com.jalicz.MojeProduktyApp.GUI.components.Panel;
import com.jalicz.MojeProduktyApp.GUI.components.Text;
import com.jalicz.MojeProduktyApp.GUI.frames.ServerSettingsFrame;
import com.jalicz.MojeProduktyApp.Start;
import com.jalicz.MojeProduktyApp.WebServer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MenuPanel extends Panel {

    private static final Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);

    private boolean serverRunning = false;
    private final Button startStopServer;

    public MenuPanel(WebServer server) {
        super(new GridLayout(1, 3));

        setBorder(margin);

        final Panel appInfoBox;
        {
            final Image appIcon = new Image("ikony/MPx256.png", 1);
            final Text appVersion = new Text("verze " + Start.appVersion);

            appInfoBox = new Panel(new BorderLayout());
            appInfoBox.setBorder(null);

            appInfoBox.add(appIcon, BorderLayout.CENTER);
            appInfoBox.add(appVersion, BorderLayout.SOUTH);
        }

        final Panel buttonsPanel;
        {
            buttonsPanel = new Panel(new GridLayout(4, 1));
            buttonsPanel.setBorder(null);

            startStopServer = new Button("");
            startStopServer.setFont(Frame.getFont(1.4));

            updateServerButton(Color.GREEN, "Spustit server");

            // po kliknutí na server-tlačítko
            startStopServer.addActionListener(e -> {
                if(serverRunning) {
                    updateServerButton(Color.GRAY, "Zastavuji server...");
                    server.stopServer();
                    updateServerButton(Color.GREEN, "Spustit server");
                    serverRunning = false;
                } else {
                    updateServerButton(Color.GRAY, "Spouštím server...");
                    server.startServer();
                    updateServerButton(Color.RED, "Zastavit server");
                    serverRunning = true;
                }
            });

            final Button serverSettingsButton = new Button("Nastavení serveru");
            serverSettingsButton.setFont(Frame.getFont(1.4));
            serverSettingsButton.setBorder(new LineBorder(new Color(0, 186, 255), 1));
            serverSettingsButton.setForeground(new Color(0, 120, 180));

            serverSettingsButton.addActionListener(e -> new ServerSettingsFrame());

            buttonsPanel.add(serverSettingsButton);
            buttonsPanel.add(startStopServer);
        }

        add(appInfoBox);
        add(buttonsPanel);
    }

    // for the first time, it should start the server
    public void clickServerButton() {
        startStopServer.doClick();
    }

    private void updateServerButton(Color color, String text) {
        startStopServer.setBorder(new LineBorder(color, 1));
        startStopServer.setForeground(color);
        startStopServer.setText(text);
    }
}

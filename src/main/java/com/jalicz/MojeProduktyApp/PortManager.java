package com.jalicz.MojeProduktyApp;

import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.files.Log;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashMap;

public class PortManager {

    public static final int defaultPort = 8080;
    private static final int MIN_PORT_NUMBER = 1024, MAX_PORT_NUMBER = 65535;
    public static int currentServerPort = -1;

    public static boolean isPortAvialable(int port) {
        if(port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) return false;

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if(ds != null) ds.close();
            if(ss != null) try { ss.close(); } catch (IOException ignored) { }
        }

        return false;
    }

    public static int getPort() {
        int port = defaultPort;
        try {
            final HashMap<String, String> map = FileManager.getAppFileMap(FileManager.serverSettingsFilePath);
            if(map == null) Log.warn("V souboru '" + FileManager.serverSettingsFilePath + "' není uvedený port, prozatím použiji výchozí (" + port + ").");
            else if(map.containsKey("port")) port = Integer.parseInt(map.get("port"));
        } catch (Exception e) {
            Log.warn("Špatně zapsaný port v souboru '" + FileManager.serverSettingsFilePath + "', prozatím použiji výchozí port (" + port + ").");
        }
        if(isPortAvialable(port)) return port;
        else if(port != defaultPort && isPortAvialable(defaultPort)) {
            Log.warn("Port '" + port + "' zapsaný v souboru " + FileManager.serverSettingsFilePath + " není dostupný (asi ho využívá jiný proces), " +
                    "prozatím použiji výchozí port '" + defaultPort + "'.");
            return defaultPort;
        }
        else {
            Log.warn("Port '" + port + "' zapsaný v souboru " + FileManager.serverSettingsFilePath + " není dostupný (asi ho využívá jiný proces), " +
                    "pokusím se najít jiný port v rozsahu 8000 až 8999 a případně ho použiji.");
            port = -1;
            for(int p=8000; p<9000; p++) {
                if(isPortAvialable(p)) {
                    port = p;
                    break;
                }
            }
            if(port == -1) Log.error("Hledal jsem široko daleko, ale nepodařilo se mi najít žádný port, na kterém bych mohl spustit server.");
            else Log.info("Našel jsem port '" + port + "', který je dostupný.");
        }
        return port;
    }
}

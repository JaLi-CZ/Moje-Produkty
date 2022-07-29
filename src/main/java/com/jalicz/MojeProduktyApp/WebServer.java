package com.jalicz.MojeProduktyApp;

import com.jalicz.MojeProduktyApp.files.FileManager;
import com.jalicz.MojeProduktyApp.files.Log;
import com.jalicz.MojeProduktyApp.files.search.response.ResponseBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;


@SpringBootApplication
@RestController
public class WebServer {

    private static final String redirectHtml = "<meta http-equiv=\"refresh\" content=\"0; url=/index.html\" />\n" +
            "<p><a href=\"/index.html\">Klikněte sem, prohlížeč Vás nepřesměroval automaticky.</a></p>";

    private int port;
    private ConfigurableApplicationContext context;

    public WebServer() {

    }

    public void startServer() {
        ResponseBuilder.updateWebStructure();
        final int port = PortManager.getPort();
        if(port == -1) {
            Log.error("Nelze spustit server - neplatný port. Prosím změňte port v souboru '" + FileManager.serverSettingsFilePath + "' nebo v nastavení serveru.");
            return;
        }
        this.port = port;

        System.getProperties().put("server.port", port);

        context = SpringApplication.run(WebServer.class);

        PortManager.currentServerPort = port;

        Log.info("Spustil jsem webový server na portu '" + port + "'.");
    }

    public void stopServer() {
        context.stop();

        PortManager.currentServerPort = -1;

        Log.info("Zastavil jsem webový server na portu '" + port + "'.");
    }

    @RequestMapping(value = {"*","*/*","*/*/*","*/*/*/*","*/*/*/*/*"})
    private ResponseEntity<String> onClientRequest(HttpServletRequest request) {
        String filePath = request.getRequestURI();
        if(filePath.startsWith("/")) filePath = filePath.substring(1);
        if(filePath.isEmpty()) return new ResponseEntity<>(redirectHtml, HttpStatus.PERMANENT_REDIRECT);

        final File file = FileManager.getWebFile(filePath);
        final HttpStatus status;
        final String content;
        if(file.exists()) {
            content = FileManager.read(file);
            status = HttpStatus.OK;
        } else {
            content = "<h1>Chyba 404</h1><h2>Soubor nenalezen :(</h2>";
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(content, status);
    }
}

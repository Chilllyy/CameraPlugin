package me.chillywilly.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import me.chillywilly.CameraPlugin;

public class WebApp extends NanoHTTPD {
    private CameraPlugin plugin;
    public WebApp(CameraPlugin plugin, int port) throws IOException {
        super(port);
        this.plugin = plugin;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        plugin.getLogger().info("Started Webserver on port: " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1> ";
        
        plugin.getLogger().info("Web Connected!");
        Map<String, List<String>> parms = session.getParameters();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username").get(0) + "!</p>";
        }
        msg = msg + "</h1></body></html>";
        return newFixedLengthResponse(msg);
    }
}

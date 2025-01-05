package me.chillywilly.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import io.javalin.util.JavalinLogger;
import me.chillywilly.CameraPlugin;
import me.chillywilly.util.PluginConst;

public class Web {
    private Javalin app;
    private int port;

    public Web(int port) {
        this.port = port;
        JavalinLogger.startupInfo = false;
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

        app.get("/image/{uuid}", ctx -> {
            String html = "<html><body>";

            String imageHtml = "<img src='data:image/png;base64, ";

            String UUID = ctx.pathParam("uuid");

            //TODO get file stuff and convert to base64

            File file = new File(PluginConst.Storage.images_folder, UUID + ".png");

            String base64_image = "";
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                base64_image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
            } catch (FileNotFoundException e) {
                CameraPlugin.plugin.getLogger().warning("Somebody tried to access an image that doesn't exist: " + UUID);
                e.printStackTrace();
            } catch (IOException e) {
                CameraPlugin.plugin.getLogger().warning("Unable to read image from disk: " + UUID);
                e.printStackTrace();
            }

            imageHtml += base64_image + "'/>";

            html += imageHtml + "</body></html>";

            ctx.status(200).html(html);
        });

        app.post("/up_post", ctx -> {
            if (!ctx.isMultipartFormData()) {
                ctx.status(400).result("Bad Request: Expected multipart/form-data");
            }

            int auth = 0;
            try { 
                auth = Integer.valueOf(ctx.formParam("auth"));
            } catch (NumberFormatException e) {
                CameraPlugin.plugin.getLogger().warning("Authorization code provided is not a number: " + ctx.formParam("auth"));
            }

            if (!CameraPlugin.plugin.companionManager.hasAuthKey(auth)) {
                ctx.status(401).result("Unauthorized: Authorization key provided is not valid");
                return;
            }

            UploadedFile file = ctx.uploadedFile("files");
            UUID uuid = UUID.randomUUID();

            //TODO check for existing UUID

            FileUtil.streamToFile(file.content(), PluginConst.Storage.images_folder + File.separator + uuid.toString() + ".png");

            CameraPlugin.plugin.companionManager.authUploadedFile(auth, uuid);
        });

        start();
    }

    public void start() {
        app.start(port);
        CameraPlugin.plugin.getLogger().info("Started Webserver");
    }

    public void stop() {
        app.stop();
        CameraPlugin.plugin.getLogger().info("Stopped Webserver");
    }
}

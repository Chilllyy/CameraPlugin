package me.chillywilly.web;

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

            FileUtil.streamToFile(file.content(), PluginConst.Storage.images_folder + uuid.toString() + ".png");

            CameraPlugin.plugin.companionManager.authUploadedFile(auth, uuid);
        });
    }
}

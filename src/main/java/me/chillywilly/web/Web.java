package me.chillywilly.web;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import io.javalin.util.JavalinLogger;
import me.chillywilly.CameraPlugin;
import me.chillywilly.util.PluginConst;

public class Web {
    private Javalin app;
    private int port;

    private List<String> existing_uuids;

    public Web(int port) {
        this.port = port;
        JavalinLogger.startupInfo = false;
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

        app.get("/image/{uuid}", ctx -> {
            File file = new File(PluginConst.Storage.web_folder, "web_image_page.html");
            String html = readFile(file);

            String UUID = ctx.pathParam("uuid");
            File image = new File(PluginConst.Storage.images_folder, UUID + ".png");

            File overlay = new File(PluginConst.Storage.overlay_folder, "test.png");

            String base64_image = "";
            String base64_overlay = "";
            int width = 0;
            int height = 0;
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(image);
                byte[] bytes = new byte[(int)image.length()];
                BufferedImage image_read = ImageIO.read(image);
                width = image_read.getWidth();
                height = image_read.getHeight();
                fileInputStreamReader.read(bytes);
                base64_image = new String(Base64.getEncoder().encode(bytes), "UTF-8");


                FileInputStream overlayStreamReader = new FileInputStream(overlay);
                byte[] overlay_bytes = new byte[(int)overlay.length()];
                overlayStreamReader.read(overlay_bytes);
                base64_overlay = new String(Base64.getEncoder().encode(overlay_bytes), "UTF-8");
            } catch (FileNotFoundException e) {
                CameraPlugin.plugin.getLogger().warning("Somebody tried to access an image that doesn't exist: " + UUID);
                e.printStackTrace();
            } catch (IOException e) {
                CameraPlugin.plugin.getLogger().warning("Unable to read image from disk: " + UUID);
                e.printStackTrace();
            }

            String canvas = "<canvas id='image' width='" + width + "' height='" + height + "'></canvas>";

            String script = getScript().replace("{image1}", base64_image).replace("{image2}", base64_overlay);

            html = html.replace("{image}", canvas).replace("{script}", script);
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

    private String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
            in.close();
        } catch (IOException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to read HTML file for webpage!");
            e.printStackTrace();
        }
        return builder.toString();
    }

    private String getScript() {
        StringBuilder builder = new StringBuilder();
        builder.append("const canvas = document.getElementById('image'); const ctx = canvas.getContext('2d');");
        builder.append("const image1 = new Image(); const image2 = new Image();");
        builder.append("image1.src = 'data:image/png;base64, {image1}'; image2.src = 'data:image/png;base64, {image2}';");
        builder.append("image1.onload = () => {");
        builder.append("ctx.drawImage(image1, 0, 0);");
        builder.append("image2.onload = () => {");
        builder.append("ctx.drawImage(image2, 0, 0, image1.width, image1.height);");
        builder.append("const downloadLink = document.createElement('a'); downloadLink.download = 'image.png';");
        builder.append("downloadLink.href = canvas.toDataURL('image/png');");
        builder.append("}}");


        return builder.toString();
    }
}

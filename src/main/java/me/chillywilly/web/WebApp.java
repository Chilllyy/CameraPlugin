package me.chillywilly.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import io.javalin.util.JavalinLogger;
import me.chillywilly.CameraPlugin;

public class WebApp {
    private CameraPlugin plugin;
    private Javalin app;
    private int port;
    public WebApp(CameraPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
        JavalinLogger.startupInfo = false;
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });
        app.get("/", ctx -> {
            File file = new File(CameraPlugin.web_root + "index.html");
            String content = readFile(file);

            ctx.html(content);
        });

        app.get("/hello/{name}", ctx -> {
            File file = new File(CameraPlugin.web_root + "index.html");
            String content = readFile(file);

            content = content.replace("{test}", ctx.pathParam("name"));

            ctx.html(content);
        });

        app.get("/image/{uuid}", ctx -> {
            String html = "<html><body>";

            String imageHtml = "<img src='data:image/png;base64, ";

            String UUID = ctx.pathParam("uuid");

            //TODO Get image and convert to base64

            File file = new File(CameraPlugin.upload_path + UUID + ".png");
            
            String base64_image = "";
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                base64_image = new String(Base64.getEncoder().encode(bytes), "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageHtml += base64_image + "'/>";

            html += imageHtml + "</body></html>";

            ctx.status(200).html(html);
        });

        app.get("/upload", ctx -> {
            File file = new File(CameraPlugin.web_root + "upload.html");
            String content = readFile(file);

            ctx.html(content);
        });

        app.post("/up_post", ctx -> {
            if (!ctx.isMultipartFormData()) {
                ctx.status(400).result("Bad Request: Expected multipart/form-data");
                return;
            }

            int auth = 0;
            try {   
                auth = Integer.valueOf(ctx.formParam("auth"));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Authorization code provided cannot be converted to number: " + ctx.formParam("auth"));
            }
            
            if (!plugin.getNetManager().getAuthList().containsKey(auth)) {
                ctx.status(401).result("Unauthorized: No Authorization key provided");
                return;
            }

            UploadedFile file = ctx.uploadedFile("files"); //Get File

            UUID uuid = UUID.randomUUID();

            FileUtil.streamToFile(file.content(), CameraPlugin.upload_path + uuid.toString() + ".png");

            plugin.getNetManager().getAuthList().get(auth).uploadImage(uuid);

            plugin.getLogger().info("Recieved Image and uploaded as: " + uuid.toString());
        });        

        start();
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
            e.printStackTrace();
        }
        return builder.toString();
    }

    public void start() {
        app.start(port);        
    }

    public void stop() {
        app.stop();
        plugin.getLogger().info("Stopped Webserver!");
    }
}

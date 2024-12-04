package me.chillywilly.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

            int auth = Integer.valueOf(ctx.formParam("auth"));

            if (!plugin.getNetManager().getAuthList().containsKey(auth)) {
                
            }

            UploadedFile file = ctx.uploadedFile("files"); //Get File
            FileUtil.streamToFile(file.content(), CameraPlugin.upload_path + file.filename()); //Temporary, will be moved to end once we have a final filename

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

package me.chillywilly;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.commands.CameraCommand;
import me.chillywilly.events.PlayerJoinCheckCompanion;
import me.chillywilly.shoots.ShootsManager;
import me.chillywilly.util.BuiltinMessages;
import me.chillywilly.util.NetManager;
import me.chillywilly.web.WebApp;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class CameraPlugin extends JavaPlugin {
    public static String data_path;
    public static String shoots_path;
    public static String web_root;
    public static String upload_path;

    private BuiltinMessages messages;
    private ShootsManager shoots;

    private NetManager netManager;
    private WebApp web;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        data_path = getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;
        shoots_path = data_path + "shoots" + File.separator;
        upload_path = data_path + "uploads" + File.separator;
        web_root = getDataFolder().getAbsolutePath() + File.separator + "web" + File.separator;

        if (new File(data_path).mkdirs()) {
            getLogger().info("Successfully created data folder");
        }
        if (new File(shoots_path).mkdirs()) {
            getLogger().info("Successfully created shoots folder");
        }
        if (new File(upload_path).mkdirs()) {
            getLogger().info("Successfully created upload path folder");
        }
        if (new File(web_root).mkdirs()) {
            getLogger().info("Successfully created webroot path");
        }

        int web_port = getConfig().getInt("web.port");

        web = new WebApp(this, web_port); //Starts webapp at port from config
        
        messages = new BuiltinMessages(this);

        shoots = new ShootsManager(this);

        getCommand("camera").setExecutor(new CameraCommand(this));

        netManager = new NetManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinCheckCompanion(this), this);
    }

    @Override
    public void onDisable() {
        if (netManager != null) netManager.disable();
        if (messages != null) messages.clearCache();
        if (shoots != null) shoots.clearCache();
        if (web != null) web.stop();
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Successfully Disabled Plugin");
    }

    public void reloadCore() {
        shoots.clearCache();
    }

    public void reloadWeb() {
        web.stop();
        web.start();
    }

    public void reloadMessages() {
        messages.clearCache();
    }

    public void reloadAll() {
        reloadCore();
        reloadWeb();
        reloadMessages();
    }

    public void sendMessage(Player player, String key) {
        Component message = messages.getMessage(key);
        Audience aud = (Audience) player;

        aud.sendMessage(message);
    }

    public void sendURLMessage(Player player, String URL) {
        Component message = messages.getMessageReplace("command.render.render-complete", "{URL}", URL);
        Audience aud = (Audience) player;
        getLogger().info("Sent Msg");
        aud.sendMessage(message);
    }

    public ShootsManager getShoots() {
        return shoots;
    }

    public NetManager getNetManager() {
        return netManager;
    }
}
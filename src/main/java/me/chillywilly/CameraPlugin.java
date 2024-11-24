package me.chillywilly;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.commands.CameraCommand;
import me.chillywilly.shoots.ShootsManager;
import me.chillywilly.util.BuiltinMessages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class CameraPlugin extends JavaPlugin {
    public static String resource_path;
    public static String data_path;
    public static String shoots_path;
    private static String output_path;

    public static double FOV;

    private BuiltinMessages messages;
    private ShootsManager shoots;
    @Override
    public void onEnable() {
        System.setProperty("java.awt.headless", "true");

        resource_path = getDataFolder().getAbsolutePath() + File.separator + "resourcepack" + File.separator + "assets" + File.separator;
        data_path = getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;
        shoots_path = data_path + "shoots" + File.separator;
        output_path = getDataFolder().getAbsolutePath() + File.separator + "output" + File.separator;

        if (new File(resource_path).mkdirs()) {
            getLogger().info("Successfully created resourcepack folder");
        }
        if (new File(data_path).mkdirs()) {
            getLogger().info("Successfully created data folder");
        }
        if (new File(shoots_path).mkdirs()) {
            getLogger().info("Successfully created shoots folder");
        }
        if (new File(output_path).mkdirs()) {
            getLogger().info("Successfully created output folder");
        }

        saveDefaultConfig();
        saveResource("messages.yml", false);
        messages = new BuiltinMessages(this);

        shoots = new ShootsManager(this);

        getCommand("camera").setExecutor(new CameraCommand(this));
    }

    public void sendMessage(Player player, String key) {
        Component message = messages.getMessage(key);
        Audience aud = (Audience) player;

        aud.sendMessage(message);
    }

    public ShootsManager getShoots() {
        return shoots;
    }

    public void reloadShoots() {
        shoots.reload();
    }

    public void resetMessageCache() {
        messages.reload();
        //TODO make messages an actual cache (loads all on start)
    }
}
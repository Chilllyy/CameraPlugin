package me.chillywilly;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.commands.CameraCommand;
import me.chillywilly.events.PlayerJoinCheckCompanion;
import me.chillywilly.shoots.ShootsManager;
import me.chillywilly.util.BuiltinMessages;
import me.chillywilly.util.NetManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class CameraPlugin extends JavaPlugin {
    public static String data_path;
    public static String shoots_path;

    private BuiltinMessages messages;
    private ShootsManager shoots;

    private NetManager netManager;
    @Override
    public void onEnable() {
        data_path = getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator;
        shoots_path = data_path + "shoots" + File.separator;

        if (new File(data_path).mkdirs()) {
            getLogger().info("Successfully created data folder");
        }
        if (new File(shoots_path).mkdirs()) {
            getLogger().info("Successfully created shoots folder");
        }

        saveResource("messages.yml", false);
        messages = new BuiltinMessages(this);

        shoots = new ShootsManager(this);

        //getCommand("camera").setExecutor(new CameraCommand(this));
        getCommand("camera").setExecutor(new CameraCommand(this));

        netManager = new NetManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinCheckCompanion(this), this);
    }

    @Override
    public void onDisable() {
        netManager.disable();
        messages.clearCache();
        shoots.clearCache();
        getServer().getScheduler().cancelTasks(this);
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
    }

    public NetManager getNetManager() {
        return netManager;
    }
}
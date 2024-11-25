package me.chillywilly.util;

import java.nio.charset.StandardCharsets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.chillywilly.CameraPlugin;

public class NetManager {
    private CameraPlugin plugin;

    public NetManager (CameraPlugin plugin) {
        this.plugin = plugin;
        enable();
    }

    public void enable() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "camera:screenshot");
    }

    public void disable() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "camera:screenshot");
    }

    public void generateAndSendAuth(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(0);
        int auth =  (int) Math.round(Math.random() * 1000);
        plugin.getLogger().info("Auth Code Being Sent: " + auth);

        out.writeInt(auth);

        player.sendPluginMessage(plugin, "camera:screenshot", out.toByteArray());
    }
}

package me.chillywilly.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.chillywilly.CameraPlugin;

public class NetManager implements PluginMessageListener {
    private CameraPlugin plugin;
    private Player companion;

    public NetManager (CameraPlugin plugin) {
        this.plugin = plugin;
        enable();
    }

    public void enable() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, NetConstants.SCREENSHOT_PACKET_ID);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, NetConstants.CHECK_FOR_COMPANION_ID);

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, NetConstants.COMPANION_FOUND_ID, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, NetConstants.SCREENSHOT_TAKEN_ID, this);
    }

    public void disable() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, NetConstants.SCREENSHOT_PACKET_ID);
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, NetConstants.CHECK_FOR_COMPANION_ID);
        
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, NetConstants.COMPANION_FOUND_ID, this);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, NetConstants.SCREENSHOT_TAKEN_ID, this);
    }

    public void findCompanion() {
        Bukkit.getOnlinePlayers().forEach((player) -> {
            send(player, NetConstants.CHECK_FOR_COMPANION_ID);
        });
    }

    public void screenshot(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(0);
        int auth = new Random().nextInt(32768);
    
        out.writeInt(auth);

        send(player, NetConstants.SCREENSHOT_PACKET_ID, out);
    }

    private void send(Player player, String channel) {
        this.send(player, channel, ByteStreams.newDataOutput());
    }

    private void send(Player player, String channel, ByteArrayDataOutput data) {
        player.sendPluginMessage(plugin, channel, data.toByteArray());
        plugin.getLogger().info("Sent Packet on channel: " + channel);
    }

    public Player getCompanion() {
        return companion;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        switch (channel) {
            case NetConstants.COMPANION_FOUND_ID:
                plugin.getLogger().info("Companion Found! (" + player.getName() + ")");
                companion = player;
                break;
            case NetConstants.SCREENSHOT_TAKEN_ID:
                //TODO Send message to all players
                break;
            default:
                plugin.getLogger().warning("Something weird happened, contact the dev with this info (Channel: " + channel + ", Player UUID: " + player.getUniqueId().toString() + ")");
                break;
        }
    }
}

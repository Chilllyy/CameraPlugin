package me.chillywilly.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.ShootInfo;
import me.chillywilly.util.PluginConst;

public class CompanionManager implements Listener, PluginMessageListener {

    private List<Player> companions;

    private HashMap<Player, Boolean> busy_map;

    public CompanionManager() {
        CameraPlugin plugin = CameraPlugin.plugin;
        companions = new ArrayList<Player>();
        busy_map = new HashMap<Player, Boolean>();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PluginConst.Network.SCREENSHOT_PACKET_ID);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PluginConst.Network.CHECK_FOR_COMPANION_ID);

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PluginConst.Network.COMPANION_FOUND_ID, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PluginConst.Network.SCREENSHOT_TAKEN_ID, this);
    }

    //Regular Events
    
    public void reload() {
        busy_map.clear();
        companions.clear();
        Bukkit.getOnlinePlayers().forEach((player) -> {
            send(player, PluginConst.Network.CHECK_FOR_COMPANION_ID);
        });
    }
    

    //Companion Control

    public boolean isCompanion(Player player) {
        return companions.contains(player);
    }

    public Player getNextAvailableCompanion() {
        for (int x = 0; x < companions.size(); x++) {
            Player player = companions.get(x);
            if (!busy_map.get(player)) {
                busy_map.put(player, true);
                return player;
            }
        }

        return null;
    }

    public void send(Player player, String channel, byte[] data) {
        player.sendPluginMessage(CameraPlugin.plugin, channel, data);
    }

    public void send(Player player, String channel) {
        send(player, channel, ByteStreams.newDataOutput().toByteArray());
    }

    public void screenshot(Player player) {
        if (companions.contains(player)) {
            send(player, PluginConst.Network.SCREENSHOT_PACKET_ID);
        }
    }

    public void generateAndSendScreenshot(Player player, ShootInfo info) {
        int auth = (int) Math.round(Math.random() * 32768);

        ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
        bytes.writeInt(0);
        bytes.writeInt(auth);

        send(player, PluginConst.Network.SCREENSHOT_PACKET_ID, bytes.toByteArray());
    }


    //Events

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) { //Join event, sends companion check after player joins
        Bukkit.getScheduler().runTaskLater(CameraPlugin.plugin, () -> {
            send(event.getPlayer(), PluginConst.Network.CHECK_FOR_COMPANION_ID);
        }, 100);
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (companions.contains(player)) {
            companions.remove(player);
            busy_map.remove(player);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) { //Plugin channel message received, used to receive packets
        switch (channel) {
            case PluginConst.Network.COMPANION_FOUND_ID:
                if (!companions.contains(player)) {
                    companions.add(player);
                    busy_map.put(player, false);
                    CameraPlugin.plugin.getLogger().info("Found companion! (" + player.getName() + ")");
                }
                break;
            case PluginConst.Network.SCREENSHOT_TAKEN_ID:
                busy_map.put(player, false);
                break;

        }
    }
}
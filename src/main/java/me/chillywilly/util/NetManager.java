package me.chillywilly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.Shoot;

public class NetManager implements PluginMessageListener {
    private CameraPlugin plugin;
    private List<Player> companion;
    private HashMap<Player, Boolean> busy;
    private HashMap<Player, Location> prev_location;
    private HashMap<Integer, Shoot> auth_list;

    public NetManager (CameraPlugin plugin) {
        this.plugin = plugin;
        this.companion = new ArrayList<Player>();
        this.busy = new HashMap<Player, Boolean>();
        this.prev_location = new HashMap<Player, Location>();
        this.auth_list = new HashMap<Integer, Shoot>();
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

        companion.clear();
        busy.clear();
        prev_location.clear();
        auth_list.clear();
    }

    public Player getAvailableCompanion() {
        Iterator<Player> it = companion.iterator();

        while (it.hasNext()) {
            Player player = it.next();
            if (!busy.get(player) && player.isOnline()) {
                return player;
            }
        }

        return null;
    }

    public void checkCompanion(Player player) {
        if (!player.isOnline()) return;

        send(player, NetConstants.CHECK_FOR_COMPANION_ID);
    }

    public void setPreviousLocation(Player player, Location location) {
        if (companion.contains(player)) {
            prev_location.put(player, location);
        }
    }

    public Location getPreviousLocation(Player player) {
        return prev_location.get(player);
    }

    public void removeCompanion(Player player) {
        companion.remove(player);
        if (busy.containsKey(player)) {
            busy.remove(player);
        }
    }

    public void screenshot(Player player, Integer auth) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(0);
        out.writeInt(auth);

        send(player, NetConstants.SCREENSHOT_PACKET_ID, out);
        busy.put(player, true);
    }

    private void send(Player player, String channel) {
        this.send(player, channel, ByteStreams.newDataOutput());
    }

    private void send(Player player, String channel, ByteArrayDataOutput data) {
        player.sendPluginMessage(plugin, channel, data.toByteArray());
        plugin.getLogger().info("Sent Packet on channel: " + channel);
    }

    public HashMap<Integer, Shoot> getAuthList() {
        return auth_list;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        switch (channel) {
            case NetConstants.COMPANION_FOUND_ID:
                plugin.getLogger().info("Companion Found! (" + player.getName() + ")");
                companion.add(player);
                busy.put(player, false);

                break;
            case NetConstants.SCREENSHOT_TAKEN_ID:
                //TODO Send message to all players
                busy.put(player, false);
                if (prev_location.containsKey(player)) {
                    player.teleport(prev_location.get(player));
                    prev_location.remove(player);
                }
                break;
            default:
                plugin.getLogger().warning("Something weird happened, contact the dev with this info (Channel: " + channel + ", Player UUID: " + player.getUniqueId().toString() + ")");
                break;
        }
    }
}

package me.chillywilly.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.chillywilly.CameraPlugin;

public class PlayerJoinCheckCompanion implements Listener {

    private CameraPlugin plugin;

    public PlayerJoinCheckCompanion(CameraPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getNetManager().checkCompanion(event.getPlayer());
            }
        }, 100);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getNetManager().removeCompanion(event.getPlayer());
    }
}

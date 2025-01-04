package me.chillywilly.network;

import java.net.http.WebSocket.Listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import me.chillywilly.CameraPlugin;

public class CompanionManager implements Listener {
    

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CameraPlugin.plugin, () -> {
            
        }, 5000);
    }
}
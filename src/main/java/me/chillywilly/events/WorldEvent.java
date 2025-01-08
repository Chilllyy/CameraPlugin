package me.chillywilly.events;

import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import me.chillywilly.CameraPlugin;

public class WorldEvent implements Listener {
    public void event(WorldLoadEvent event) {
        CameraPlugin.plugin.reload(1);
    }
}

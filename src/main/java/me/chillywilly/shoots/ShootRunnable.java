package me.chillywilly.shoots;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ShootRunnable implements Runnable {
    private ShootInfo info;
    private float countdown_clock;
    private Integer assignedTaskId;
    private Player companion;
    public ShootRunnable (ShootInfo info) {
        this.info = info;
        countdown_clock = info.getTimer();
        companion = CameraPlugin.plugin.companionManager.getNextAvailableCompanion();

        if (companion != null) {
            companion.teleport(info.getCameraLocation());
        } else {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(info.getSenseLocation()) <= 20) {
                    player.sendMessage("No Camera Found!");
                }
            });

            return;
        }

        scheduleTimer();
    }

    @Override
    public void run() {
        if (countdown_clock < 1) { //Timer Complete
            //Timer is done, take screenshot

            if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
            return;
        }

        Bukkit.getOnlinePlayers().forEach((player) -> {
            if (player.getLocation().distance(info.getSenseLocation()) <= 20) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Taking Photo in: " + (int) this.countdown_clock));
            }
        });
        
        countdown_clock--;
    }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CameraPlugin.plugin, this, 0, 20);
    }
}

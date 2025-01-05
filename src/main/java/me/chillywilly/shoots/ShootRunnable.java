package me.chillywilly.shoots;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ShootRunnable implements Runnable {
    private ShootInfo info;
    private float countdown_clock;
    private Integer assignedTaskId;
    private Player companion;
    private Location old_camera_location;
    public ShootRunnable (ShootInfo info) {
        this.info = info;
        countdown_clock = info.getTimer();
        companion = CameraPlugin.plugin.companionManager.getNextAvailableCompanion();
        info.setInUse(true);

        if (companion != null) {
            old_camera_location = companion.getLocation();
            companion.teleport(info.getCameraLocation());
        } else {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(info.getSenseLocation()) <= 20) {
                    player.sendMessage("No Camera Found!");
                    info.setInUse(false);
                }
            });

            return;
        }

        scheduleTimer();
    }

    @Override
    public void run() {
        if (countdown_clock < 0) { //Timer Complete
            //Timer is done, take screenshot
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(info.getSenseLocation()) <= 20) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                }
            });

            CameraPlugin.plugin.companionManager.generateAndSendScreenshot(companion, info);

            Bukkit.getScheduler().runTaskLater(CameraPlugin.plugin, () -> {
                companion.teleport(old_camera_location);
            }, 40);

            Bukkit.getScheduler().runTaskLater(CameraPlugin.plugin, () -> {
                info.setInUse(false);
            }, 200);

            if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
            return;
        }

        Bukkit.getOnlinePlayers().forEach((player) -> {
            if (player.getLocation().distance(info.getSenseLocation()) <= 20) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Taking Photo in: " + (int) this.countdown_clock));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        });
        
        countdown_clock--;
    }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CameraPlugin.plugin, this, 0, 20);
    }
}

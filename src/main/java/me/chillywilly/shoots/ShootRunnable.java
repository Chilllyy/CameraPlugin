package me.chillywilly.shoots;

import java.util.ArrayList;
import java.util.List;

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
    private List<Player> player_list;
    public ShootRunnable (ShootInfo info) {
        this.info = info;
        player_list = new ArrayList<Player>();
        countdown_clock = info.getTimer();
        companion = CameraPlugin.plugin.companionManager.getNextAvailableCompanion();

        if (companion != null) {
            info.setInUse(true);
            old_camera_location = companion.getLocation();
            companion.teleport(info.getCameraLocation());
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(info.getSenseLocation()) <= 15) {
                    if (!player.getUniqueId().equals(companion.getUniqueId())) {
                        player_list.add(player);
                    }
                }
            });
        } else {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(info.getSenseLocation()) <= 15) {
                    CameraPlugin.plugin.messages.sendMessage(player, "render.no-camera-found");
                    info.setInUse(false);
                }
            });
            return;
        }

        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CameraPlugin.plugin, this, 0, 20);
    }

    @Override
    public void run() {
        if (countdown_clock < 0) { //Timer Complete
            //Timer is done, take screenshot
            player_list.forEach((player) -> {
                if (player.isOnline()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                } else {
                    player_list.remove(player);
                }
            });
            CameraPlugin.plugin.companionManager.generateAndSendScreenshot(companion, info, this);

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
            if (player.getLocation().distance(info.getSenseLocation()) <= 15) {
                if (player.isOnline()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Taking Photo in: " + (int) this.countdown_clock));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    if (!player_list.contains(player)) {
                        player_list.add(player);
                    }
                } else {
                    player_list.remove(player);
                }
            }
        });
        
        countdown_clock--;
    }

    public List<Player> getPlayers() {
        return player_list;
    }

    public void delete() {
        player_list.clear();
    }

    public Player getCompanion() {
        return companion;
    }

    public ShootInfo getInfo() {
        return info;
    }
}

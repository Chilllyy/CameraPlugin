package me.chillywilly.shoots;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.chillywilly.CameraPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Shoot {
    private CameraPlugin plugin;
    
    private File shoot_file;
    
    private Location cameraLocation;

    private Location senseLocation;

    private float range;

    private float timer;

    private BukkitTask timerTask;

    public Boolean in_use = false;
    
    public Shoot(File shoot_file, CameraPlugin plugin) {
        this.shoot_file = shoot_file;
        this.plugin = plugin;
        range = 5;

        FileConfiguration yaml = YamlConfiguration.loadConfiguration(shoot_file);

        this.cameraLocation = yaml.getLocation("camera.location");
        this.senseLocation = yaml.getLocation("sense.location");
        this.range = yaml.getInt("sense.range");
    }

    //Functions

    public boolean delete() {
        return shoot_file.delete();
    }

    public boolean rename(File new_file) {
        if (new_file.exists()) {
            return false;
        }
        shoot_file.renameTo(new_file);
        this.shoot_file = new_file;
        return true;
    }

    private boolean updateFile() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shoot_file);
        if (cameraLocation != null) yaml.set("camera.location", cameraLocation);

        if (senseLocation != null) yaml.set("sense.location", senseLocation);
        
        yaml.set("sense.range", range);

        try {
            yaml.save(shoot_file);
            return true;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
    }

    public void render(float timer) {
        if (in_use) return;
        in_use = true;

        this.timer = timer;
        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (this.timer <= 0) {
                Player companion = plugin.getNetManager().getAvailableCompanion();
                if (companion != null) {
                    plugin.getNetManager().setPreviousLocation(companion, companion.getLocation());
                    companion.teleport(cameraLocation);
                    plugin.getNetManager().screenshot(companion);
                }

                Bukkit.getOnlinePlayers().forEach((player) -> {
                    if (player.getLocation().distance(cameraLocation) <= 20) {
                        if (companion == null) {
                            plugin.sendMessage(player, "command.render.no-account-online");
                        } else {
                            plugin.sendMessage(player, "command.render.render-complete");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                    }
                });
                in_use = false;
                timerTask.cancel();
            }

            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getLocation().distance(cameraLocation) <= 20) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Taking Photo in: " + (int) this.timer));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                }
            });

            this.timer--;
        }, 20, 20);
    }

    //Getters

    public File getFile() {
        return shoot_file;
    }

    public Location getCameraLocation() {
        return cameraLocation;
    }

    public Location getSenseLocation() {
        return senseLocation;
    }

    public float getRange() {
        return range;
    }

    //Setters

    public boolean setCamera(Location location) {
        this.cameraLocation = location;
        return updateFile();
    }

    public boolean setSense(Location location) {
        this.senseLocation = location;
        return updateFile();
    }

    public boolean setRange(float range) {
        this.range = range;
        return updateFile();
    }
}

package me.chillywilly.command;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.ShootInfo;
import me.chillywilly.shoots.ShootRunnable;
import me.chillywilly.util.PluginConst;

public class CameraCommandUtils {
    public static void setupShoot(Player player, String shootname, String setup, String argument) {
        CameraPlugin.plugin.getLogger().info("Setup Shoot: (" + shootname + ", " + setup + ", " + argument + ")");
        ShootInfo info = CameraPlugin.plugin.shootManager.getShoot(shootname);
        if (info == null) {
            player.sendMessage("Shoot does not exist");
            return;
        }

        switch (setup) {
            case "camera":
                if (info.setCameraLocation(player.getLocation())) {
                    player.sendMessage("Successfully set location");
                    return;
                }
                player.sendMessage("Unable to set location");
                return;
            case "sense":
                if (info.setSenseLocation(player.getLocation())) {
                    player.sendMessage("Successfully set location");
                    return;
                }

                player.sendMessage("Unable to set location");
                return;
            case "shootLocation":
                if (info.isRollercoaster()) {
                    if (info.setShootLocation(player.getLocation())) {
                        player.sendMessage("Successfully set location");
                        return;
                    }
                    player.sendMessage("Unable to set location");
                    return;
                }
                player.sendMessage("Shoot is not a rollercoaster");
                return;
            case "range":
                if (argument != null) {
                    try {
                        Float range = Float.parseFloat(argument);
                        if (info.setRange(range)) {
                            player.sendMessage("Successfully set range");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("Unknown number provided");
                        return;
                    }
                    player.sendMessage("Unable to set range");
                    return;
                }
                player.sendMessage("Argument is not a number");
                return;
            case "timer":
                if (argument != null) {
                    try {
                        Float timer = Float.parseFloat(argument);
                        if (info.setTimer(timer)) {
                            player.sendMessage("Successfully set range");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("Unknown number provided");
                        return;
                    }
                    player.sendMessage("Unable to set timer");
                    return;
                }
                player.sendMessage("Argument is not a number");
                return;
            case "overlay":
                File[] overlays = PluginConst.Storage.overlay_folder.listFiles();
                for (File overlay : overlays) {
                    if (overlay.getName().split("\\.")[0].equalsIgnoreCase(argument)) {
                        if (info.setOverlay(argument)) {
                            player.sendMessage("Successfully set overlay");
                            return;
                        }
                        player.sendMessage("Unable to set overlay");
                        return;
                    }
                }

                player.sendMessage("No overlay found by that name");
                return;
        }
    }

    public static void createShoot(Player player, String shootname, String type) {
        CameraPlugin.plugin.getLogger().info("Create Shoot: (" + shootname + ", " + type + ")");
        if (CameraPlugin.plugin.shootManager.createShoot(player.getLocation(), shootname, type)) {
            player.sendMessage("Successfully created shoot!");
        } else {
            player.sendMessage("Could not create shoot, it may already exist");
        }
    }

    public static void deleteShoot(Player player, String shootname) {
        CameraPlugin.plugin.getLogger().info("Delete Shoot: (" + shootname + ")");
        if (CameraPlugin.plugin.shootManager.deleteShoot(shootname)) {
            player.sendMessage("Successfully deleted shoot!");
        } else {
            player.sendMessage("Could not delete shoot");
        }
    }

    public static void renameShoot(Player player, String shootname, String newname) {
        CameraPlugin.plugin.getLogger().info("Rename Shoot: (" + shootname + ", " + newname + ")");
        ShootInfo info = CameraPlugin.plugin.shootManager.getShoot(shootname);
        if (info != null) {
            if (info.rename(newname)) {
                player.sendMessage("Successfully renamed file");
            } else {
                player.sendMessage("Unable to rename file");
            }
        } else {
            player.sendMessage("Invalid shoot name, try again!");
        }
    }

    public static void render(CommandSender sender, String shootname, Float timer) {
        CameraPlugin.plugin.getLogger().info("Render Shoot: (" + shootname + ", " + timer + ")");
        ShootInfo info = CameraPlugin.plugin.shootManager.getShoot(shootname);
        new ShootRunnable(info);
        sender.sendMessage("Successfully rendered!");
    }


    public static void reload(CommandSender sender) {
        CameraPlugin.plugin.reload(0);
        sender.sendMessage("Reloaded All");
    }

    public static void reloadWeb(CommandSender sender) {
        CameraPlugin.plugin.reload(2);
        sender.sendMessage("Reloaded Web");
    }

    public static void reloadCore(CommandSender sender) {
        CameraPlugin.plugin.reload(1);
        sender.sendMessage("Reloaded Core");
    }
}
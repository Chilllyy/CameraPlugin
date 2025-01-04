package me.chillywilly.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.ShootInfo;

public class CameraCommandUtils {
    public static void setupShoot(Player player, String shootname, String setup, Float argument) {
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
                    if (info.setRange(argument)) {
                        player.sendMessage("Successfully set range");
                        return;
                    }
                    player.sendMessage("Unable to set range");
                    return;
                }
                player.sendMessage("Argument is not a number");
                return;
            case "timer":
                if (argument != null) {
                    if (info.setTimer(argument)) {
                        player.sendMessage("Successfully set timer");
                        return;
                    }
                    player.sendMessage("Unable to set timer");
                    return;
                }
                player.sendMessage("Argument is not a number");
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
    }


    public static void reload(CommandSender sender) {
        CameraPlugin.plugin.getLogger().info("Reload All!");
        CameraPlugin.plugin.reload(0);
        //TODO reload web
    }

    public static void reloadWeb(CommandSender sender) {
        CameraPlugin.plugin.getLogger().info("Reload Web!");
        CameraPlugin.plugin.reload(2);
    }

    public static void reloadCore(CommandSender sender) {
        CameraPlugin.plugin.getLogger().info("Reload Core!");
        CameraPlugin.plugin.reload(1);
    }
}
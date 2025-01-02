package me.chillywilly.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;

public class CameraCommandUtils {
    public static void setupShoot(Player player, String shootname, String setup, Float argument) {
        if (argument == null) {
            CameraPlugin.plugin.getLogger().info("Setup Shoot: (" + shootname + ", " + setup + ")");
        } else {
            CameraPlugin.plugin.getLogger().info("Setup Shoot: (" + shootname + ", " + setup + ", " + argument + ")");
        }
    }

    public static void createShoot(Player player, String shootname, String type) {
        CameraPlugin.plugin.getLogger().info("Create Shoot: (" + shootname + ", " + type + ")");
    }

    public static void deleteShoot(Player player, String shootname) {
        CameraPlugin.plugin.getLogger().info("Delete Shoot: (" + shootname + ")");
    }

    public static void renameShoot(Player player, String shootname, String newname) {
        CameraPlugin.plugin.getLogger().info("Rename Shoot: (" + shootname + ", " + newname + ")");
    }

    public static void render(CommandSender sender, String shootname, Float timer) {
        CameraPlugin.plugin.getLogger().info("Render Shoot: (" + shootname + ", " + timer + ")");
    }
}
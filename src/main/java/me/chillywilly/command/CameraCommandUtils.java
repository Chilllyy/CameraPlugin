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
        ShootInfo info = CameraPlugin.plugin.shootManager.getShoot(shootname);
        if (info == null) {
            CameraPlugin.plugin.messages.sendMessage(player, "command.setup.does-not-exist");
            return;
        }

        switch (setup) {
            case "camera":
                if (info.setCameraLocation(player.getLocation())) {
                    CameraPlugin.plugin.messages.sendMessage(player, "command.setup.camera.success");
                    return;
                }
                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.camera.fail");
                return;
            case "sense":
                if (info.setSenseLocation(player.getLocation())) {
                    CameraPlugin.plugin.messages.sendMessage(player, "command.setup.sense.success");
                    return;
                }

                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.sense.fail");
                return;
            case "shootLocation":
                if (info.isRollercoaster()) {
                    if (info.setShootLocation(player.getLocation())) {
                        CameraPlugin.plugin.messages.sendMessage(player, "command.setup.shoot_location.success");
                        return;
                    }
                    CameraPlugin.plugin.messages.sendMessage(player, "command.setup.shoot_location.fail");
                    return;
                }
                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.shoot_location.not-rollercoaster");
                return;
            case "range":
                if (argument != null) {
                    try {
                        Float range = Float.parseFloat(argument);
                        if (info.setRange(range)) {
                            CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.success");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.unknown-number");
                        return;
                    }
                    CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.fail");
                    return;
                }
                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.none-provided");
                return;
            case "timer":
                if (argument != null) {
                    try {
                        Float timer = Float.parseFloat(argument);
                        if (info.setTimer(timer)) {
                            CameraPlugin.plugin.messages.sendMessage(player, "command.setup.timer.success");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        CameraPlugin.plugin.messages.sendMessage(player, "command.setup.timer.unknown-number");
                        return;
                    }
                    CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.fail");
                    return;
                }
                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.range.none-provided");
                return;
            case "overlay":
                File[] overlays = PluginConst.Storage.overlay_folder.listFiles();
                for (File overlay : overlays) {
                    if (argument.equalsIgnoreCase("reset")) {
                        if (info.setOverlay(null)) {
                            CameraPlugin.plugin.messages.sendMessage(player, "command.setup.overlay.success");
                            return;
                        }
                        CameraPlugin.plugin.messages.sendMessage(player, "command.setup.overlay.fail");
                        return;
                    }
                    if (overlay.getName().split("\\.")[0].equalsIgnoreCase(argument)) {
                        if (info.setOverlay(argument)) {
                            CameraPlugin.plugin.messages.sendMessage(player, "command.setup.overlay.success");
                            return;
                        }
                        CameraPlugin.plugin.messages.sendMessage(player, "command.setup.overlay.fail");
                        return;
                    }
                }
                CameraPlugin.plugin.messages.sendMessage(player, "command.setup.overlay.none-found");
                return;
        }
    }

    public static void createShoot(Player player, String shootname, String type) {
        if (CameraPlugin.plugin.shootManager.createShoot(player.getLocation(), shootname, type)) {
            CameraPlugin.plugin.messages.sendMessage(player, "command.create.success");
        } else {
            CameraPlugin.plugin.messages.sendMessage(player, "command.create.fail");
        }
    }

    public static void deleteShoot(Player player, String shootname) {
        if (CameraPlugin.plugin.shootManager.deleteShoot(shootname)) {
            CameraPlugin.plugin.messages.sendMessage(player, "command.delete.success");
        } else {
            CameraPlugin.plugin.messages.sendMessage(player, "command.delete.fail");
        }
    }

    public static void renameShoot(Player player, String shootname, String newname) {
        ShootInfo info = CameraPlugin.plugin.shootManager.getShoot(shootname);
        if (info != null) {
            if (info.rename(newname)) {
                CameraPlugin.plugin.messages.sendMessage(player, "command.rename.success");
            } else {
                CameraPlugin.plugin.messages.sendMessage(player, "command.rename.fail");
            }
        } else {
            CameraPlugin.plugin.messages.sendMessage(player, "command.rename.doesnt-exist");
        }
    }

    public static void render(CommandSender sender, String shootname, Float timer) {
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
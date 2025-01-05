package me.chillywilly;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.command.CameraCommand;
import me.chillywilly.network.CompanionManager;
import me.chillywilly.shoots.ShootInfo;
import me.chillywilly.shoots.ShootManager;
import me.chillywilly.shoots.ShootRunnable;
import me.chillywilly.util.PluginConst;

public class CameraPlugin extends JavaPlugin {

    public static CameraPlugin plugin;
    public ShootManager shootManager;
    public CompanionManager companionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        CameraCommand command = new CameraCommand();

        getCommand("camera").setExecutor(command);
        getCommand("camera").setTabCompleter(command);

        CameraPlugin.plugin = this;

        PluginConst.Storage.init(); //Init all storage folders and constant variables (For accessing files)
        shootManager = new ShootManager(); //Init Shoot Manager

        companionManager = new CompanionManager();
        getServer().getPluginManager().registerEvents(new CompanionManager(), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            /*
             * This runs every 2 seconds or so, it checks all of the shoot locations and sees if a player is nearby
             */

            Collection<ShootInfo> shoots = shootManager.getShoots();
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (!companionManager.isCompanion(player)) {
                    shoots.forEach((shoot) -> {
                        if (player.getLocation().distance(shoot.getSenseLocation()) <= shoot.getRange()) {
                            //TODO start shoot
                            player.sendMessage("Start Shoot Countdown!");

                            new ShootRunnable(shoot);
                        }
                    });
                }
            });
        }, 0, 40);
    }

    public void reload(int reload) {
        /*
         * 0: Reload All
         * 1: Reload Core
         * 2: Reload Web
         */
        switch (reload) {
            case 0:
                shootManager.reload();
                companionManager.reload();
                break;
            case 1:
                shootManager.reload();
                companionManager.reload();
                break;
            case 2:
                //TODO reload web
                break;
        }
    }
}
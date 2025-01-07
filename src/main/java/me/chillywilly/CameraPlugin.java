package me.chillywilly;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.command.CameraCommand;
import me.chillywilly.network.CompanionManager;
import me.chillywilly.shoots.ShootInfo;
import me.chillywilly.shoots.ShootManager;
import me.chillywilly.shoots.ShootRunnable;
import me.chillywilly.util.DatabaseManager;
import me.chillywilly.util.PluginConst;
import me.chillywilly.web.Web;

public class CameraPlugin extends JavaPlugin {

    public static CameraPlugin plugin;
    public ShootManager shootManager;
    public CompanionManager companionManager;
    public DatabaseManager database;
    private Web web;

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
                        if (!shoot.in_use() && player.getLocation().distance(shoot.getSenseLocation()) <= shoot.getRange()) {
                            player.sendMessage("Start Shoot Countdown!");

                            new ShootRunnable(shoot);
                        }
                    });
                }
            });
        }, 0, 40);

        database = new DatabaseManager();
        database.sqlite();

        web = new Web(getConfig().getInt("web.port"));
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
                reloadConfig();
                reloadWeb();
                break;
            case 1:
                shootManager.reload();
                companionManager.reload();
                reloadConfig();
                break;
            case 2:
                reloadWeb();
                break;
        }
    }

    private void reloadWeb() {
        web.stop();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            web = new Web(plugin.getConfig().getInt("web.port"));
        }, 100);
    }
}
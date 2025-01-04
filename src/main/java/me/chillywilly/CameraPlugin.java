package me.chillywilly;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.command.CameraCommand;
import me.chillywilly.network.CompanionManager;
import me.chillywilly.shoots.ShootManager;
import me.chillywilly.util.PluginConst;

public class CameraPlugin extends JavaPlugin {

    public static CameraPlugin plugin;
    public ShootManager shootManager;
    private CompanionManager companionManager;

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
        getServer().getPluginManager().registerEvents((Listener) companionManager, this);
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
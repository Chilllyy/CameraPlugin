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

    @Override
    public void onEnable() {
        saveDefaultConfig();

        CameraCommand command = new CameraCommand();

        getCommand("camera").setExecutor(command);
        getCommand("camera").setTabCompleter(command);

        CameraPlugin.plugin = this;

        PluginConst.Storage.init(); //Init all storage folders and constant variables (For accessing files)
        shootManager = new ShootManager(); //Init Shoot Manager

        getServer().getPluginManager().registerEvents((Listener) new CompanionManager(), this);
    }

    public void reload() {
        shootManager.reload();
    }
}
package me.chillywilly;

import org.bukkit.plugin.java.JavaPlugin;

import me.chillywilly.command.CameraCommand;

public class CameraPlugin extends JavaPlugin {

    public static JavaPlugin plugin;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        CameraCommand command = new CameraCommand();

        getCommand("camera").setExecutor(command);
        getCommand("camera").setTabCompleter(command);

        CameraPlugin.plugin = this;
    }
}
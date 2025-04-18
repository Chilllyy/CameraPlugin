package me.chillywilly.shoots;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import me.chillywilly.CameraPlugin;
import me.chillywilly.util.PluginConst;

public class ShootInfo {
    private String name;
    private File file;
    private YamlConfiguration fileConfig;
    private String type;
    private Location camera_location;
    private Location sense_location;
    private Location shoot_location; //shoot location, only for rollercoaster type
    private String overlay;

    private boolean in_use;

    private float range;
    private float timer;

    //Init
    public ShootInfo(Location location, File file, String type) {
        this.file = file;
        fileConfig = YamlConfiguration.loadConfiguration(file);
        name = file.getName().split("\\.")[0];
        this.type = type;
        camera_location = location;
        sense_location = new Location(location.getWorld(), 0, 0, 0);
        if (type == "rollercoaster") {
            shoot_location = location;
        }
        range = 3.0f;
        timer = 5.0f;
        

        fileConfig.set("type", type);
        fileConfig.set("camera_location", camera_location);
        fileConfig.set("sense_location", sense_location);
        if (type == "rollercoaster") {
            fileConfig.set("rollercoaster.shoot_location", location);
        }

        fileConfig.set("range", range);
        fileConfig.set("timer", timer);
        fileConfig.set("overlay", overlay);

        save();

    }

    public ShootInfo(File file) {
        this.file = file;
        fileConfig = YamlConfiguration.loadConfiguration(file);
        name = file.getName().split("\\.")[0]; //First part of file name
        type = fileConfig.getString("type");
        camera_location = fileConfig.getLocation("camera_location");
        sense_location = fileConfig.getLocation("sense_location");
        if (type == "rollercoaster") {
            shoot_location = fileConfig.getLocation("rollercoaster.shoot_location");
        }

        range = (float) fileConfig.getDouble("range");
        timer = (float) fileConfig.getDouble("timer");
        overlay = fileConfig.getString("overlay");
    }

    //Outer Functions

    public boolean rename(String newname) {
        name = newname;
        File new_file = new File(PluginConst.Storage.shoots_folder, newname + ".yml");
        if (new_file.exists()) return false;

        file.renameTo(new_file);
        fileConfig = YamlConfiguration.loadConfiguration(new_file);

        file.delete();
        CameraPlugin.plugin.shootManager.reload();
        return true;
    }

    public boolean delete() {
        return file.delete();
    }

    //Inner Functions

    private boolean save() {
        fileConfig.set("range", range);
        fileConfig.set("camera_location", camera_location);
        fileConfig.set("sense_location", sense_location);
        fileConfig.set("timer", timer);
        fileConfig.set("overlay", overlay);
        if (type == "rollercoaster") {
            fileConfig.set("rollercoaster.shoot_location", shoot_location);
        }
        try {
            fileConfig.save(file);
            return true;
        } catch (IOException e) {
            CameraPlugin.plugin.getLogger().warning("IO exception while saving shoot file: " + file.getName());
            e.printStackTrace();
        }
        return false;
    }

    //Setters

    public boolean setRange(float range) {
        this.range = range;
        return save();
    }

    public boolean setTimer(float timer) {
        this.timer = timer;
        return save();
    }

    public boolean setCameraLocation(Location camera_location) {
        this.camera_location = camera_location;
        return save();
    }

    public boolean setSenseLocation(Location sense_location) {
        this.sense_location = sense_location;
        return save();
    }

    public boolean setShootLocation(Location shoot_location) {
        this.shoot_location = shoot_location;
        return save();
    }

    public void setInUse(boolean in_use) {
        this.in_use = in_use;
    }

    public boolean setOverlay(String overlay) {
        this.overlay = overlay;
        return save();
    }

    //Getters

    public float getRange() {
        return range;
    }

    public float getTimer() {
        return timer;
    }

    public Location getCameraLocation() {
        return camera_location;
    }

    public Location getSenseLocation() {
        return sense_location;
    }

    public Location getShootLocation() {
        return shoot_location;
    }

    public String getName() {
        return name;
    }

    public boolean isRollercoaster() {
        return type.equalsIgnoreCase("rollercoaster");
    }

    public File getFile() {
        return file;
    }

    public boolean in_use() {
        return in_use;
    }

    public String getOverlay() {
        return overlay;
    }
}
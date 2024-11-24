package me.chillywilly.shoots;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Shoot {
    private File shoot_file;
    
    private Location cameraLocation;

    private Location senseLocation;

    private float range;

    public Shoot(File shoot_file) {
        this.shoot_file = shoot_file;
        range = 5;

        FileConfiguration yaml = YamlConfiguration.loadConfiguration(shoot_file);

        this.cameraLocation = yaml.getLocation("camera.location");
        this.senseLocation = yaml.getLocation("sense.location");
        this.range = yaml.getInt("sense.range");
    }

    //Functions

    public boolean delete() {
        return shoot_file.delete();
    }

    public boolean rename(File new_file) {
        if (new_file.exists()) {
            return false;
        }
        shoot_file.renameTo(new_file);
        this.shoot_file = new_file;
        return true;
    }

    private boolean updateFile() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(shoot_file);
        if (cameraLocation != null) yaml.set("camera.location", cameraLocation);

        if (senseLocation != null) yaml.set("sense.location", senseLocation);
        
        yaml.set("sense.range", range);

        try {
            yaml.save(shoot_file);
            return true;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
    }

    //Getters

    public File getFile() {
        return shoot_file;
    }

    public Location getCameraLocation() {
        return cameraLocation;
    }

    public Location getSenseLocation() {
        return senseLocation;
    }

    public float getRange() {
        return range;
    }

    //Setters

    public boolean setCamera(Location location) {
        this.cameraLocation = location;
        return updateFile();
    }

    public boolean setSense(Location location) {
        this.senseLocation = location;
        return updateFile();
    }

    public boolean setRange(float range) {
        this.range = range;
        return updateFile();
    }
}

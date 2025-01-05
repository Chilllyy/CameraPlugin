package me.chillywilly.shoots;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

import me.chillywilly.util.PluginConst;

public class ShootManager {
    private HashMap<File, ShootInfo> shoots;
    private HashMap<String, File> shoot_files;

    public ShootManager() {
        shoots = new HashMap<File, ShootInfo>();
        shoot_files = new HashMap<String, File>();
        initShoots();
    }

    //Init

    private void initShoots() {
        File[] shoots_in_folder = PluginConst.Storage.shoots_folder.listFiles();

        for (File shoot_file : shoots_in_folder) {
            ShootInfo info = new ShootInfo(shoot_file);
            shoots.put(shoot_file, info);
            shoot_files.put(info.getName(), shoot_file);
        }
    }

    //Regular Functions

    public void reload() {
        shoots.clear();
        shoot_files.clear();
        initShoots();
    }

    public boolean createShoot(Location location, String name, String type) {
        if (shoot_files.containsKey(name)) return false;
        File file = new File(PluginConst.Storage.shoots_folder, name + ".yml");
        shoot_files.put(name, file);
        shoots.put(file, new ShootInfo(location, file, type));
        return true;
    }

    public boolean deleteShoot(String name) {
        ShootInfo info = getShoot(name);
        File file = info.getFile();
        if (info.delete()) {
            shoot_files.remove(name);
            shoots.remove(file);
            return true;
        } else {
            return false;
        }
    }

    //Getter

    public List<String> getShootNames() {
        List<String> shoot_names = new ArrayList<String>();
        shoots.values().forEach((shoot) -> {
            shoot_names.add(shoot.getName());
        });

        return shoot_names;
    }

    public ShootInfo getShoot(File file) {
        return shoots.get(file);
    }

    public ShootInfo getShoot(String string) {
        if (shoot_files.containsKey(string)) {
            return shoots.get(shoot_files.get(string));
        }
        return null;
    }

    public Collection<ShootInfo> getShoots() {
        return shoots.values();
    }
}
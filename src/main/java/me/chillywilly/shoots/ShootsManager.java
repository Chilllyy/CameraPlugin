package me.chillywilly.shoots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.chillywilly.CameraPlugin;

public class ShootsManager{
    private List<Shoot> shootList;

    private CameraPlugin plugin;

    public ShootsManager(CameraPlugin plugin) {
        this.plugin = plugin;

        //Init
        shootList = new ArrayList<Shoot>();
        
        fillCache();
    }

    public void reload() {
        clearCache();
        fillCache();
    }

    public void clearCache() {
        shootList.clear();
    }

    private void fillCache() {
        File file = new File(CameraPlugin.shoots_path);
        File[] active_shoots = file.listFiles();

        for (int x = 0; x < active_shoots.length; x++) {
            loadShoot(active_shoots[x]);
        }
    }

    public boolean deleteShoot(File file) {
        Shoot shoot = get(file);

        shootList.remove(shoot);
        return shoot.delete();
    }

    public boolean rename(File old_name, File new_name) {
        Shoot old_shoot = get(old_name);
        return old_shoot.rename(new_name);
    }

    public void loadShoot(File file) {
        shootList.add(new Shoot(file, plugin));
    }

    public int createShoot(File file) {
        //1 = complete!
        //2 = exists
        //3 = ioexception
        if (file.exists()) {
            return 2;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("camera.location", null);

        config.set("sense.location", null);
        config.set("sense.range", null);

        try {
            config.save(file);
            shootList.add(new Shoot(file, plugin));
            return 1;
        } catch (IOException io) {
            plugin.getLogger().warning("Unable to create shoots file!");
            io.printStackTrace();
            return 3;
        }
    }

    public Shoot get(File file) {
        if (shootList == null) return null;
        Iterator<Shoot> it = shootList.iterator();
        
        while (it.hasNext()) {
            Shoot shoot = it.next();
            File shot_file = shoot.getFile();
            if (file.equals(shot_file)) {
                return shoot;
            }
        }
        return null;
    }

    public List<String> getList() {
        List<String> list = new ArrayList<String>();
        for (int x = 0; x < shootList.size(); x++) {
            String name = shootList.get(x).getFile().getName();
            list.add(name.replace(".yml", ""));
        }

        return list;
    }
}
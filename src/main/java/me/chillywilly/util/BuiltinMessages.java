package me.chillywilly.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.chillywilly.CameraPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BuiltinMessages {
    private HashMap<String, Component> messages;
    private CameraPlugin plugin;
    public BuiltinMessages(CameraPlugin plugin) {
        this.plugin = plugin;
        messages = new HashMap<String, Component>();
        fillCache();
    }

    public void reload() {
        clearCache();
        fillCache();
    }

    private void fillCache() {
        Reader reader = new InputStreamReader(plugin.getResource("messages.yml"));
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(reader);

        Set<String> keys = yaml.getKeys(true);

        keys.forEach((key) -> {
            if (yaml.isString(key)) {
                getMessage(key);
            }
        });
    }

    private void clearCache() {
        messages.clear();
    }


    public Component getMessage(String key) {
        Component message = loadFromMem(key);
        if (message != null) return message;

        FileConfiguration file = getMessageFile();

        String file_msg = file.getString(key);

        if (!file.contains(key)) {
            Reader reader = new InputStreamReader(plugin.getResource("messages.yml"));
            FileConfiguration internal_file = YamlConfiguration.loadConfiguration(reader);

            file_msg = internal_file.getString(key);
            file.set(key, file_msg);
            try {
                file.save(plugin.getDataFolder() + File.separator + "messages.yml");
                plugin.getLogger().info("Unable to find key (" + key + ") in messages.yml, loaded from internal.");
            } catch (IOException io) {
                plugin.getLogger().warning("Unable to set key (" + key + ") in messages.yml");
                io.printStackTrace();
            }
        }

        if (file_msg.contains("{prefix}") && key != "plugin.prefix") {
            Component prefix = getMessage("plugin.prefix");
            String prefix_string = MiniMessage.miniMessage().serialize(prefix);
            file_msg = file_msg.replace("{prefix}", prefix_string);
        }

        String converted_msg = convertLegacyToMiniMessage(file_msg);

        message = MiniMessage.miniMessage().deserialize(converted_msg);
        messages.put(key, message);
        return message;
    }

    private FileConfiguration getMessageFile() {
        File file = new File(plugin.getDataFolder() + File.separator + "messages.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private Component loadFromMem(String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        }
        return null;
    }

    private String convertLegacyToMiniMessage(String message) {
        String miniMessage = message
        .replace("&0", "<black>")
        .replace("&1", "<dark_blue>")
        .replace("&2", "<dark_green>")
        .replace("&3", "<dark_aqua>")
        .replace("&4", "<dark_red>")
        .replace("&5", "<dark_purple>")
        .replace("&6", "<gold>")
        .replace("&7", "<gray>")
        .replace("&8", "<dark_gray>")
        .replace("&9", "<blue>")
        .replace("&a", "<green>")
        .replace("&b", "<aqua>")
        .replace("&c", "<red>")
        .replace("&d", "<light_purple>")
        .replace("&e", "<yellow>")
        .replace("&f", "<white>")
        .replace("&l", "<bold>")
        .replace("&m", "<strikethrough>")
        .replace("&n", "<underline>")
        .replace("&o", "<italic>")
        .replace("&k", "<obfuscated>");

        return miniMessage;
    }
}

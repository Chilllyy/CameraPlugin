package me.chillywilly.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Messages {
    private HashMap<String, Component> messages;
    
    public Messages() {
        messages = new HashMap<>();
        if (new File(CameraPlugin.plugin.getDataFolder(), "messages.yml").exists()) {
            CameraPlugin.plugin.saveResource("messages.yml", false);
        }

        fill();
    }

    //Outer Functions

    public void reload() {
        messages.clear();
        fill();
    }

    public void sendMessage(Player player, String key) {
        Component message = getMessage(key);
        Audience aud = (Audience) player;
        aud.sendMessage(message);
    }

    public void sendURLMessage(Player player, String URL) {
        Component message = getMessage("render.completed");
        message.replaceText(TextReplacementConfig.builder().match("{url}").replacement(URL).build());
        message.clickEvent(ClickEvent.openUrl(URL));
        Audience aud = (Audience) player;
        aud.sendMessage(message);
    }

    //Inner Functions

    private Component getMessage(String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        }

        File file = new File(CameraPlugin.plugin.getDataFolder(), "messages.yml");
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String file_msg = yaml.getString(key);

        if (!yaml.contains(key)) {
            Reader reader = new InputStreamReader(CameraPlugin.plugin.getResource("messages.yml"));
            FileConfiguration internal_file = YamlConfiguration.loadConfiguration(reader);

            file_msg = internal_file.getString(key);
            yaml.set(key, file_msg);
            try {
                yaml.save(file);
                CameraPlugin.plugin.getLogger().info("Unable to find key (" + key + ") in messages.yml, loaded from internal.");
            } catch (IOException e) {
                CameraPlugin.plugin.getLogger().warning("Unable to set key (" + key + ") in messages.yml");
                e.printStackTrace();
            }
        }

        file_msg = parseVanilla(file_msg);

        if (file_msg.contains("{prefix}") && key != "plugin.prefix") {
            Component prefix = getMessage("plugin.prefix");
            String prefix_string = MiniMessage.miniMessage().serialize(prefix);
            file_msg = file_msg.replace("{prefix}", prefix_string);
        }

        return MiniMessage.miniMessage().deserialize(file_msg);
    }

    private String parseVanilla(String message) {
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

    private void fill() {
        Reader reader = new InputStreamReader(CameraPlugin.plugin.getResource("messages.yml"));
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(reader);

        Set<String> keys = yaml.getKeys(true);

        keys.forEach((key) -> {
            if (yaml.isString(key)) {
                getMessage(key);
            }
        });
    }
}

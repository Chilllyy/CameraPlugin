package me.chillywilly.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.spi.DirStateFactory.Result;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;

public class DatabaseManager {
    private Connection connection;

    //Init Functions
    public void sqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/Camera/web.db");
            init();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        String initImagesDB = "CREATE TABLE IF NOT EXISTS `images` (id INTEGER PRIMARY KEY, uuid VARCHAR(255), overlay VARCHAR(255), date DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String initPlayersDB = "CREATE TABLE IF NOT EXISTS `players` (id INTEGER PRIMARY KEY, image_id int, player_uuid VARCHAR(255), player_name VARCHAR(255))";
        send(initImagesDB);
        send(initPlayersDB);
    }

    //Outer Functions

    public int insertImage(UUID uuid, String overlay) {
        try {
            String command = String.format("INSERT INTO `images` (uuid, overlay) VALUES ('%s', '%s')", uuid.toString(), overlay);
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            return keys.getInt(1);
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to insert image into web DB: " + uuid.toString() + ", " + overlay);
            e.printStackTrace();
        }

        return -1;
    }

    public void insertPlayer(int image_id, Player player) {
        try {
            String command = String.format("INSERT INTO `players` (image_id, player_uuid, player_name) VALUES (%s, '%s', '%s')", image_id, player.getUniqueId().toString(), player.getName());
            connection.prepareStatement(command).executeUpdate();
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to insert player into images: " + image_id + ", " + player.getName());
            e.printStackTrace();
        }
    }

    public int getImageId(String uuid) {
        try {
            String command = String.format("SELECT `id` FROM `images` WHERE `uuid` == '%s'", uuid);
            ResultSet result = connection.prepareStatement(command).executeQuery();
            return result.getInt(1);
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to get Image ID from UUID: " + uuid);
            e.printStackTrace();
        }

        return -1;
    }

    public String getImageOverlay(String uuid) {
        try {
            String command = String.format("Select `overlay` from `images` WHERE `uuid` == '%s'", uuid);
            ResultSet result = connection.prepareStatement(command).executeQuery();
            return result.getString("overlay");
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to get image overlay from UUID: " + uuid);
            e.printStackTrace();
        }

        return "null";
    }

    public List<OfflinePlayer> getPlayersFromImage(int image_id) {
        List<OfflinePlayer> players = new ArrayList<>();
        try {
            String command = String.format("SELECT `player_uuid` FROM `players` WHERE `image_id` == %s", image_id);
            ResultSet result = connection.prepareStatement(command).executeQuery();

            while (result.next()) {
                String uuid = result.getString("player_uuid");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                players.add(player);
            }

            return players;
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to grab players from web DB: " + image_id);
            e.printStackTrace();
        }

        return null;
    }

    public Date getDateForImage(int image_id) {
        try {
            String command = String.format("SELECT `date` FROM `images` WHERE `uuid` == `%s`", image_id);
            return connection.prepareStatement(command).executeQuery().getDate("date");
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to get daet from web DB: " + image_id);
            e.printStackTrace();
        }

        return null;
    }


    //Inner Functions

    private ResultSet insert(String command) {
        try {
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            return statement.getGeneratedKeys();
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to insert into SQL table: " + command);
            e.printStackTrace();
        }

        return null;
    }

    private void send(String command) {
        try {
            connection.prepareStatement(command).executeUpdate();
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to execute sql command: " + command);
            e.printStackTrace();
        }
    }

    private ResultSet queryDB(String command) {
        try {
            return connection.prepareStatement(command).executeQuery();
        } catch (SQLException e) {
            CameraPlugin.plugin.getLogger().warning("Unable to execute SQL query: " + command);
            e.printStackTrace();
        }
        return null;
    }
}

package me.chillywilly.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.ShootInfo;

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
        String initPlayersDB = "CREATE TABLE IF NOT EXISTS `players` (id INTEGER PRIMARY KEY, image_id int, player_uuid VARCHAR(255), player_name VARCHAR(255));";
        send(initImagesDB);
        send(initPlayersDB);
    }

    //Outer Functions


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

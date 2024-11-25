package me.chillywilly.commands;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.chillywilly.CameraPlugin;

public class TempCommand implements CommandExecutor {
    private CameraPlugin plugin;

    public TempCommand(CameraPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only applicable to players!");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage("Packets Sent!");
        Player companion = plugin.getNetManager().getCompanion();

        if (companion != null && companion.isOnline()) {
            plugin.getNetManager().screenshot(companion);
            return true;
        }

        plugin.getNetManager().findCompanion();

        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                while ((plugin.getNetManager().getCompanion() == null) || !plugin.getNetManager().getCompanion().isOnline()) {}

                plugin.getNetManager().screenshot(plugin.getNetManager().getCompanion());
            }

            
        });

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (plugin.getNetManager().getCompanion() == null || !plugin.getNetManager().getCompanion().isOnline()) {
                    task.cancel();
                    plugin.sendMessage(player, "command.render.no-account-online");

                    plugin.getLogger().info("Companion Check: (NullCheck: " + (plugin.getNetManager().getCompanion() == null) + ")");
                    if (companion != null) {
                        plugin.getLogger().info("Companion Check: (Online Check: " + plugin.getNetManager().getCompanion().isOnline() + ")");
                    }
                }
            }
        }, 200);
        
        return true;
    }
}

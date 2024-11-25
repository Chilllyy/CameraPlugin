package me.chillywilly.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        plugin.sendMessage(player, "command.render.render-complete");
        plugin.getNetManager().generateAndSendAuth(player);
        return true;
    }
}

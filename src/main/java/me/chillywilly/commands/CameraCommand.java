package me.chillywilly.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.Shoot;

public class CameraCommand implements TabExecutor {
    private CameraPlugin plugin;

    public CameraCommand(CameraPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length < 1) {
            arg0.sendMessage("Usage: /camera (setup | render | manage | reload | create)");
            return true;
        }

        if (!(arg0 instanceof Player))  {
            arg0.sendMessage("This command can not be run as console!"); 
            return true;
        }

        Player player = (Player) arg0;
        switch (arg3[0].toLowerCase()) {
            case "setup":
                if (arg3.length < 3) {
                    player.sendMessage("Usage: /camera setup (name of photo) (camera | sense | range) (range in blocks *Only for Range*)");
                    return true;
                }
                if (arg3[2].equalsIgnoreCase("range") && arg3.length < 4) {
                    player.sendMessage("Usage: /camera setup (name of photo) range (number of blocks)");
                    return true;
                }

                Shoot shoot = plugin.getShoots().get(new File(CameraPlugin.shoots_path + arg3[1] + ".yml"));
                if (shoot == null) {
                    plugin.sendMessage(player, "command.setup.no-shoot-error");
                    return true;
                }
                Location player_location = player.getLocation();
                switch(arg3[2]) {
                    case "camera":
                        if (shoot.setCamera(player_location)) {
                            plugin.sendMessage(player, "command.setup.camera-complete");
                            return true;
                        }
                        plugin.sendMessage(player, "command.setup.io-error");
                        return true;
                    case "sense":
                        if (shoot.setSense(player_location)) {
                            plugin.sendMessage(player, "command.setup.sense-complete");
                            return true;
                        }
                        plugin.sendMessage(player, "command.setup.io-error");
                        return true;
                    case "range":
                        if (shoot.setRange(Float.parseFloat(arg3[3]))) {
                            plugin.sendMessage(player, "command.setup.range-complete");
                            return true;
                        }
                        plugin.sendMessage(player, "command.setup.io-error");
                        return true;
                    default:
                        player.sendMessage("Usage: /camera setup (name of photo) (camera | pos1 | pos2 | sense | range) (range *Only for Range*)");
                        return true;
                }

            case "render":
                if (arg3.length < 2) {
                    plugin.sendMessage(player, "command.render.no-render-provided");
                    return true;
                }
                float timer = 5;
                if (arg3.length >= 3) {
                    try {
                        float arg_timer = Float.parseFloat(arg3[2]);
                        timer = arg_timer;
                    } catch (NullPointerException e) {
                        plugin.sendMessage(player, "command.render.render-timer-not-number");
                        return true;
                    }
                }

                Shoot shot = plugin.getShoots().get(new File(CameraPlugin.shoots_path + arg3[1] + ".yml"));

                //TODO Render

                return true;

            case "manage":
                if (arg3.length < 3) {
                    player.sendMessage("Usage: /camera manage (name of photo) (rename | delete)");
                    return true;
                }

                String file_name = "";
                switch (arg3[2].toLowerCase()) {
                    case "delete":
                        file_name = arg3[1] + ".yml";
                        File file = new File(CameraPlugin.shoots_path + file_name);
                        if (plugin.getShoots().deleteShoot(file)) {
                            plugin.sendMessage(player, "command.manage.delete.delete-complete");
                            return true;
                        }
                        plugin.sendMessage(player, "command.manage.delete.delete-error");
                        return true;
                    case "rename":
                        if (arg3.length < 4) {
                            player.sendMessage("Usage: /camera manage (name of photo) rename (new name)");
                            return true;
                        }
                        file_name = arg3[1] + ".yml";
                        String new_name = arg3[3] + ".yml";
                        
                        File old_file = new File(CameraPlugin.shoots_path + file_name);
                        File new_file = new File(CameraPlugin.shoots_path + new_name);

                        if (plugin.getShoots().rename(old_file, new_file)) {
                            plugin.sendMessage(player, "command.manage.rename.rename-complete");
                            return true;
                        }
                        plugin.sendMessage(player, "command.manage.rename.rename-error-exists");
                        return true;
                }
                break;
            case "reload":
                if (arg3.length > 2) {
                    //Reload Module
                    switch (arg3[1].toLowerCase()) {
                        case "web":
                            //TODO web reload
                            return true;
                        case "messages":
                            plugin.resetMessageCache();
                            plugin.sendMessage(player, "command.reload.reload-message-complete");
                            return true;
                        case "core":
                            //TODO core reload
                            plugin.reloadShoots();
                            return true;
                        case "default":
                            //All Reload, just falls off to the regular command
                    }
                }


                plugin.resetMessageCache();
                plugin.reloadShoots();
                plugin.sendMessage(player, "command.reload.reload-complete");
                break;
            case "create":
                if (arg3.length < 2) {
                    player.sendMessage("/camera create (name of photo)");
                    return true;
                }


                File file = new File(CameraPlugin.shoots_path + File.separator + arg3[1].toLowerCase().replace(" ", "_") + ".yml");

                switch (plugin.getShoots().createShoot(file)) {
                    case 1:
                        plugin.sendMessage(player, "command.create.create-complete");
                        return true;
                    case 2:
                        plugin.sendMessage(player, "command.create.create-exists");
                        return true;
                    case 3:
                        plugin.sendMessage(player, "command.create.create-io");
                        return true;
                    default:
                        plugin.getLogger().warning("Unknown error during shoot creation!");
                        return true;
                }

            default:
                player.sendMessage("Usage: /camera (setup | render | manage | reload | create)");
                return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<String>();

        switch (args.length) {
            case 1:
                StringUtil.copyPartialMatches(args[0], List.of("setup", "render", "manage", "reload", "create"), options);
                break;
            case 2:
                switch (args[0]) { 
                    case "reload": //camera reload
                        StringUtil.copyPartialMatches(args[1], List.of("web", "messages", "core", "all"), options);
                        break;
                    case "create":
                        StringUtil.copyPartialMatches(args[1], List.of("New Name"), options);
                        break;
                    default:
                        List<String> shoot_list = new ArrayList<String>();
                        shoot_list.add("Name of photo");
                        shoot_list.addAll(plugin.getShoots().getList());
                        StringUtil.copyPartialMatches(args[1], shoot_list, options);
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "manage": //camera manage (name) ()
                        StringUtil.copyPartialMatches(args[2], List.of("rename", "delete"), options);
                        break;
                    case "setup": //camera setup (name) ()
                        StringUtil.copyPartialMatches(args[2], List.of("camera", "sense", "range"), options);
                        break;
                }
                break;
            case 4:
                if (args[0].equalsIgnoreCase("manage") && args[2].equalsIgnoreCase("rename")) {
                    StringUtil.copyPartialMatches(args[3], List.of("New Name"), options);
                }
                break;
        }

        return options;
    }
}

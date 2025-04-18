package me.chillywilly.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.chillywilly.CameraPlugin;
import me.chillywilly.shoots.ShootInfo;
import me.chillywilly.util.PluginConst;

public class CameraCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)  {
            sender.sendMessage("Usage: /camera (setup | create | delete | rename | render | reload)");
            return true;
        }
        String subcommand = args[0];
        if (args.length < 2) {
            switch (subcommand.toLowerCase()) {
                case "setup":
                    sender.sendMessage("Usage: /camera setup (name) (range | camera | timer | shootLocation (rollercoaster))");
                    return true;
                case "create":
                    sender.sendMessage("Usage: /camera create (name) [static | rollercoaster]");
                    return true;
                case "delete":
                    sender.sendMessage("Usage: /camera delete (name)");
                    return true;
                case "rename":
                    sender.sendMessage("Usage: /camera rename (name) (New Name)");
                    return true;
                case "render":
                    sender.sendMessage("Usage: /camera render (name) [Timer]");
                    return true;
                case "reload":
                    CameraCommandUtils.reload(sender);
                    return true;
                default:
                    sender.sendMessage("Usage: /camera (setup | create | delete | rename | render)");
                    return true;
            }
        }

        String shoot_name = args[1];

        if (args.length < 3) {
            switch (subcommand.toLowerCase()) {
                case "setup":
                    sender.sendMessage("Usage: /camera setup (name) (range | camera | timer | sense | shootLocation (rollercoaster))");
                    return true;
                case "create":
                    break; //3rd argument is optional, not required, defaults to static
                case "delete":
                    break; //3rd argument not needed
                case "rename":
                    sender.sendMessage("Usage: /camera rename (name) (New Name)");
                    return true;
                case "render":
                    break; //3rd argument is optional, not required, defaults to 5
                case "reload":
                    if (args[1].equalsIgnoreCase("web")) {
                        CameraCommandUtils.reloadWeb(sender);
                    } else if (args[1].equalsIgnoreCase("core")) {
                        CameraCommandUtils.reloadCore(sender);
                    } else {
                        CameraCommandUtils.reload(sender);
                    }
                    return true;
                default:
                    sender.sendMessage("Usage: /camera (setup | create | delete | rename | render)");
                    return true;
            }
        }

        if (args.length < 4) {
            switch (subcommand.toLowerCase()) {
                case "setup":
                    if (args[2].equalsIgnoreCase("timer")) {
                        sender.sendMessage("Usage: /camera setup (name) timer (timer)");
                        return true;
                    }

                    if (args[2].equalsIgnoreCase("range")) {
                        sender.sendMessage("Usage: /camera setup (name) range (range)");
                        return true;
                    }
                    break;
                case "create":
                    if (args.length > 2) {
                        switch (args[2].toLowerCase()) {
                            case "static":
                                break; //Do nothing, this is correct
                            case "rollercoaster":  
                                break; //Do nothing, this is correct
                            default:
                                sender.sendMessage("Usage: /camera create (name) [static | rollercoaster]");
                                return true;
                        }
                    }
                    break;
                case "delete":
                    break;
                case "rename":
                    break;
                case "render":
                    break;
            }
        }

        if (subcommand.equalsIgnoreCase("render")) { //Render command is handled here, because it can be run through console
            if (args.length >= 3) {
                try {
                    CameraCommandUtils.render(sender, shoot_name, Float.parseFloat(args[2]));
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("Provided item is not a number: " + args[2]);
                    return true;
                }
            }

            CameraCommandUtils.render(sender, shoot_name, -1.0f);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player!");
            return true;
        }

        Player player = (Player) sender;

        switch (subcommand.toLowerCase()) {
            case "setup":
                if (args.length >= 4) {
                    CameraCommandUtils.setupShoot(player, shoot_name, args[2].toLowerCase(), args[3]);
                    return true;
                }
                CameraCommandUtils.setupShoot(player, shoot_name, args[2].toLowerCase(), null);
                return true;
            case "create":
                if (args.length < 3) { 
                    CameraCommandUtils.createShoot(player, shoot_name, "static"); //Static is the default 
                    return true;
                }
                CameraCommandUtils.createShoot(player, shoot_name, args[2].toLowerCase());
                return true;
            case "rename":
                CameraCommandUtils.renameShoot(player, shoot_name, args[2]);
                return true;
            case "delete":
                CameraCommandUtils.deleteShoot(player, shoot_name);
                return true;
            case "render":
                return true; //Render command handled above
            default:
                CameraPlugin.plugin.getLogger().warning("Unknown subcommand! (" + subcommand + ", " + args + ")");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> ret = new ArrayList<String>();
        switch (args.length) {
            case 1:
                StringUtil.copyPartialMatches(args[0], List.of("setup", "create", "delete", "rename", "render", "reload"), ret);
                break;
            case 2:
                if (args[0].equalsIgnoreCase("create")) return List.of("New Photoshoot"); //don't return list of shoots when user is creating one
                if (args[0].equalsIgnoreCase("reload")) return List.of("web", "core", "all");
                StringUtil.copyPartialMatches(args[1], CameraPlugin.plugin.shootManager.getShootNames(), ret);
                break;
            case 3:
                //(/camera (args[0]) (args[1]) (THIS IS THE ARG))
                String shoot_identifier = args[1];
                switch (args[0]) {
                    case "setup":
                        ShootInfo shoot = CameraPlugin.plugin.shootManager.getShoot(shoot_identifier);
                        if (shoot != null && shoot.isRollercoaster()) {
                            StringUtil.copyPartialMatches(args[2], List.of("range", "camera", "timer", "sense", "shootLocation", "overlay"), ret);
                        }
                        StringUtil.copyPartialMatches(args[2], List.of("range", "camera", "timer", "sense", "overlay"), ret);
                        break;
                    case "create":
                        StringUtil.copyPartialMatches(args[2], List.of("static", "rollercoaster"), ret);
                        break;
                    case "delete":
                        break;
                    case "rename":
                        return List.of("New Name");
                    case "render":
                        return List.of("Timer");
                }
                break;
            case 4:
                //(/camera (args[0]) (args[1]) (args[2]) (THIS IS THE ARG))
                if (args[0].equalsIgnoreCase("setup")) {
                    switch (args[2].toLowerCase()) {
                        case "timer":
                            return List.of("Timer");
                        case "range":
                            return List.of("Range");
                        case "overlay":
                            File[] files = PluginConst.Storage.overlay_folder.listFiles();
                            List<String> file_names = new ArrayList<String>();
                            for (File file : files) {
                                String name = file.getName().split("\\.")[0];
                                file_names.add(name);
                            }

                            StringUtil.copyPartialMatches(args[3], file_names, ret);
                    }
                }
        }

        return ret;
    }
}
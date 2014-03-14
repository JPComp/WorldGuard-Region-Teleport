package br.AtomGamers.WRP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandSetup implements CommandExecutor {

    public static RegionTeleport plugin;

    public CommandSetup(RegionTeleport plugin) {
        CommandSetup.plugin = plugin;
    }

    protected static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    protected static void saveConfig() {
        plugin.saveConfig();
    }

    protected static boolean enabled() {
        if (getConfig().getBoolean("UseTeleportMessage")) {
            return true;
        } else {
            return false;
        }
    }

    protected static String format(Player sender) {
        String format = getConfig().getString("TeleportMessage");
        Location l = sender.getLocation();

        format = format.replaceAll("@posX", l.getBlockX() + "");
        format = format.replaceAll("@posY", l.getBlockY() + "");
        format = format.replaceAll("@posZ", l.getBlockZ() + "");
        format = format.replaceAll("@posYaw", l.getYaw() + "");
        format = format.replaceAll("@posPitch", l.getPitch() + "");
        format = format.replaceAll("@posWorld", l.getWorld().getName());
        format = format.replaceAll("@posArea", SimpleRegionManager.getAreaName(sender));
        format = format.replaceAll("&", "§");

        return "§1[RegionTeleport] §r" + format;
    }

    protected static String getSavedAreas() {
        String list = "";
        for (String n : getConfig().getConfigurationSection("Region").getKeys(false)) {
            if (n != null) {
                if (list.length() == 0) {
                    list = n;
                } else {
                    list += "§4, §2" + n;
                }
            }
        }
        if (list.isEmpty()) {
            return "§cNenhum(a)";
        } else {
            return list;
        }
    }

    protected static String getLocationTo(String area) {
        int x = getAreaLocation(area).getBlockX();
        int y = getAreaLocation(area).getBlockY();
        int z = getAreaLocation(area).getBlockZ();
        float yaw = getAreaLocation(area).getYaw();
        float pitch = getAreaLocation(area).getPitch();
        String w = getAreaLocation(area).getWorld().getName();

        return w + "/" + x + "/" + y + "/" + z + "/" + yaw + "/" + pitch;
    }

    protected static boolean hasConfigArea(String area) {
        if (getConfig().contains("Region." + area)) {
            return true;
        } else {
            return false;
        }
    }

    protected void addArea(Player sender, String area, String w) {
        Location l = sender.getLocation();

        int x, y, z;
        float yaw, pitch;
        String world1 = l.getWorld().getName();

        x = l.getBlockX();
        y = l.getBlockY();
        z = l.getBlockZ();
        yaw = l.getYaw();
        pitch = l.getPitch();

        getConfig().set("Region." + area + ".x", x);
        getConfig().set("Region." + area + ".y", y);
        getConfig().set("Region." + area + ".z", z);
        getConfig().set("Region." + area + ".yaw", yaw);
        getConfig().set("Region." + area + ".pitch", pitch);
        getConfig().set("Region." + area + ".world", world1);
        getConfig().set("Region." + area + ".WorldGuardRegion.world", w);
        saveConfig();
    }

    protected static Location getAreaLocation(String area) {
        int x, y, z;
        float yaw, pitch;
        World w;

        x = getConfig().getInt("Region." + area + ".x");
        y = getConfig().getInt("Region." + area + ".y");
        z = getConfig().getInt("Region." + area + ".z");
        yaw = getConfig().getInt("Region." + area + ".yaw");
        pitch = getConfig().getInt("Region." + area + ".pitch");
        w = Bukkit.getWorld(getConfig().getString("Region." + area + ".world"));

        return new Location(w, x + 0.5D, y + 0.5D, z + 0.5D, yaw, pitch);
    }

    protected static void delArea(Player sender, String area) {
        getConfig().set("Region." + area, null);
        saveConfig();
    }

    protected static boolean isCorrectRegionWorld(Player sender, String area) {
        World w = sender.getLocation().getWorld();
        if (getConfig().getString("Region." + area + ".WorldGuardRegion.world").equals(w.getName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String str, String[] args) {

        if (!(cs instanceof Player)) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage("§4[RegionTeleport] §fComandos bloqueados no Console.");
            return true;
        }

        Player sender = (Player) cs;

        if (cmd.getName().equalsIgnoreCase("regiontp")) {
            if (sender.hasPermission("regionteleport.admin")) {
                if (args.length == 0) {
                    sender.sendMessage("§1[RegionTeleport] §f/regiontp <criar/deletar/listar/profile>");
                } else {
                    if (args[0].equalsIgnoreCase("criar")) {
                        if (args.length < 3) {
                            sender.sendMessage("§1[RegionTeleport] §f/regiontp criar (Região) (Mundo da região)");
                        } else if (args.length >= 3) {
                            World w = Bukkit.getWorld(args[2]);
                            if (w != null) {
                                if (SimpleRegionManager.existArea(args[1], w)) {
                                    addArea(sender, args[1], w.getName());
                                    sender.sendMessage("§1[RegionTeleport] §fRegião encontrada no WorldGuard!");
                                    sender.sendMessage("§1[RegionTeleport] §fRegião salva na Config do Plugin.");
                                    sender.sendMessage("§1[RegionTeleport] §fAgora quando entrarem na região serão teleportados para cá!");
                                } else {
                                    sender.sendMessage("§4[RegionTeleport] §fNão existe nenhuma área do WorldGuard salva com este nome neste mundo!");
                                }
                            } else {
                                sender.sendMessage("§4[RegionTeleport] §cO Mundo digitado não é existente.");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("deletar")) {
                        if (args.length == 1) {
                            sender.sendMessage("§1[RegionTeleport] §f/regiontp deletar (Região)");
                        } else if (args.length > 1) {
                            if (!hasConfigArea(args[1])) {
                                sender.sendMessage("§4[RegionTeleport] §fEssa área ainda não foi salva na configuração do Plugin!");
                            } else if (hasConfigArea(args[1])) {
                                delArea(sender, args[1]);
                                sender.sendMessage("§1[RegionTeleport] §fÁrea deletada da configuração do Plugin!");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("listar")) {
                        if (args.length >= 1) {
                            sender.sendMessage("§1[RegionTeleport] §fÁreas salvas na configuração do Plugin: ");
                            sender.sendMessage("§1[RegionTeleport] §2" + getSavedAreas());
                        }
                    } else if (args[0].equalsIgnoreCase("profile")) {
                        if (args.length == 1) {
                            sender.sendMessage("§1[RegionTeleport] §f/regiontp profile (Região)");
                        } else if (args.length > 1) {
                            if (hasConfigArea(args[1])) {
                                sender.sendMessage("§1[RegionTeleport] §fInformações encontradas:");
                                sender.sendMessage(" ");
                                sender.sendMessage("§1[RegionTeleport] §fNome: §2" + args[1]);
                                sender.sendMessage("§1[RegionTeleport] §fLocal para teleporte abaixo:");
                                sender.sendMessage("§1[RegionTeleport] §f" + getLocationTo(args[1]));
                            } else {
                                sender.sendMessage("§4[RegionTeleport] §fArea não encontrada na configuração do Plugin.");
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage("§5[RegionTeleport] §fVocê não tem permissão.");
            }
        }
        return false;
    }
}
